package com.vztekoverflow.cilostazol.runtime.symbols;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.staticobject.StaticShape;
import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cil.parser.cli.signature.ElementTypeFlag;
import com.vztekoverflow.cil.parser.cli.signature.TypeSpecSig;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.generated.*;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.exceptions.InvalidCLIException;
import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;
import com.vztekoverflow.cilostazol.exceptions.TypeSystemException;
import com.vztekoverflow.cilostazol.nodes.CILOSTAZOLFrame;
import com.vztekoverflow.cilostazol.runtime.objectmodel.StaticField;
import com.vztekoverflow.cilostazol.runtime.objectmodel.StaticObject;
import com.vztekoverflow.cilostazol.runtime.symbols.utils.CLIFileUtils;
import com.vztekoverflow.cilostazol.runtime.symbols.utils.NamedTypeSymbolLayout;
import com.vztekoverflow.cilostazol.runtime.symbols.utils.NamedTypeSymbolSemantics;
import com.vztekoverflow.cilostazol.runtime.symbols.utils.NamedTypeSymbolVisibility;
import java.util.ArrayList;
import java.util.List;

public class NamedTypeSymbol extends TypeSymbol {
  private static final int ABSTRACT_FLAG_MASK = 0x80;
  private static final int SEALED_FLAG_MASK = 0x100;
  private static final int SPECIAL_NAME_FLAG_MASK = 0x400;
  private static final int IMPORT_FLAG_MASK = 0x1000;
  private static final int SERIALIZABLE_FLAG_MASK = 0x2000;
  private static final int BEFORE_FIELD_INIT_FLAG_MASK = 0x100000;
  private static final int RT_SPECIAL_NAME_FLAG_MASK = 0x800;
  private static final int HAS_SECURITY_FLAG_MASK = 0x40000;
  private static final int IS_TYPE_FORWARDER_FLAG_MASK = 0x200000;
  protected final int flags;
  protected final String name;
  protected final String namespace;
  protected final TypeParameterSymbol[] typeParameters;
  protected final TypeMap map;
  protected final CLITablePtr definingRow;
  @CompilerDirectives.CompilationFinal protected NamedTypeSymbol lazyDirectBaseClass;

  @CompilerDirectives.CompilationFinal(dimensions = 1)
  protected NamedTypeSymbol[] lazyInterfaces;

  @CompilerDirectives.CompilationFinal(dimensions = 1)
  protected MethodSymbol[] lazyMethods;

  @CompilerDirectives.CompilationFinal(dimensions = 1)
  protected MethodSymbol[] lazyVMethodTable;

  @CompilerDirectives.CompilationFinal(dimensions = 1)
  protected FieldSymbol[] lazyFields;

  @CompilerDirectives.CompilationFinal
  private StaticShape<StaticObject.StaticObjectFactory> instanceShape;

  @CompilerDirectives.CompilationFinal
  private StaticShape<StaticObject.StaticObjectFactory> staticShape;

  @CompilerDirectives.CompilationFinal(dimensions = 1)
  private StaticField[] instanceFields;

  @CompilerDirectives.CompilationFinal(dimensions = 1)
  private StaticField[] staticFields;

  @CompilerDirectives.CompilationFinal private NamedTypeSymbol superClass;

  protected NamedTypeSymbol(
      ModuleSymbol definingModule,
      int flags,
      String name,
      String namespace,
      TypeParameterSymbol[] typeParameters,
      CLITablePtr definingRow) {
    this(
        definingModule,
        flags,
        name,
        namespace,
        typeParameters,
        definingRow,
        new TypeMap(new TypeParameterSymbol[0], new TypeSymbol[0]));
  }

  protected NamedTypeSymbol(
      ModuleSymbol definingModule,
      int flags,
      String name,
      String namespace,
      TypeParameterSymbol[] typeParameters,
      CLITablePtr definingRow,
      TypeMap map) {
    super(definingModule, CILOSTAZOLFrame.getStackTypeKind(name, namespace));
    assert definingRow.getTableId() == CLITableConstants.CLI_TABLE_TYPE_DEF;

    this.flags = flags;
    this.name = name;
    this.namespace = namespace;
    this.typeParameters = typeParameters;
    this.definingRow = definingRow;
    this.map = map;
  }

  // region Getters
  public String getName() {
    return name;
  }

  public String getNamespace() {
    return namespace;
  }

  public NamedTypeSymbol getDirectBaseClass() {
    if (lazyDirectBaseClass == null) {
      CompilerDirectives.transferToInterpreterAndInvalidate();
      lazyDirectBaseClass = LazyFactory.createDirectBaseClass(this);
    }

    return lazyDirectBaseClass;
  }

  public NamedTypeSymbol[] getInterfaces() {
    if (lazyInterfaces == null) {
      CompilerDirectives.transferToInterpreterAndInvalidate();
      lazyInterfaces = LazyFactory.createInterfaces(this);
    }

    return lazyInterfaces;
  }

  public MethodSymbol[] getMethods() {
    if (lazyMethods == null) {
      CompilerDirectives.transferToInterpreterAndInvalidate();
      // TODO: create on demand
      lazyMethods =
          switch (definingRow.getTableId()) {
            case CLITableConstants.CLI_TABLE_TYPE_DEF -> LazyFactory.createMethods(
                this,
                definingModule
                    .getDefiningFile()
                    .getTableHeads()
                    .getTypeDefTableHead()
                    .skip(definingRow));
            case CLITableConstants.CLI_TABLE_TYPE_REF -> null; // TODO: implement case for ref table
            case CLITableConstants
                .CLI_TABLE_TYPE_SPEC -> null; // TODO: implement case for spec table
            default -> throw new TypeSystemException(
                CILOSTAZOLBundle.message("typeSystem.unknownTableType"));
          };
    }

    return lazyMethods;
  }

  public MethodSymbol[] getVMT() {
    throw new NotImplementedException();
  }

  public FieldSymbol[] getFields() {
    if (lazyFields == null) {
      CompilerDirectives.transferToInterpreterAndInvalidate();
      lazyFields = LazyFactory.createFields(this);
    }

    return lazyFields;
  }

  public ConstructedNamedTypeSymbol construct(TypeSymbol[] typeArguments) {
    return ConstructedNamedTypeSymbol.ConstructedNamedTypeSymbolFactory.create(
        this, this, typeArguments);
  }

  public TypeSymbol[] getTypeArguments() {
    return getTypeParameters();
  }

  public TypeParameterSymbol[] getTypeParameters() {
    return typeParameters;
  }

  public TypeMap getTypeMap() {
    return map;
  }

  public NamedTypeSymbolVisibility getVisibility() {
    return NamedTypeSymbolVisibility.fromFlags(flags & NamedTypeSymbolVisibility.MASK);
  }

  public NamedTypeSymbolLayout getLayout() {
    return NamedTypeSymbolLayout.fromFlags(flags & NamedTypeSymbolLayout.MASK);
  }

  public NamedTypeSymbolSemantics getSemantics() {
    return NamedTypeSymbolSemantics.fromFlags(flags & NamedTypeSymbolSemantics.MASK);
  }

  public boolean isSealed() {
    return (flags & SEALED_FLAG_MASK) != 0;
  }

  public boolean isAbstract() {
    return (flags & ABSTRACT_FLAG_MASK) != 0;
  }

  public boolean isInterface() {
    return getSemantics() == NamedTypeSymbolSemantics.Interface;
  }

  public boolean isClass() {
    return !isInterface();
  }

  public boolean isSpecialName() {
    return (flags & SPECIAL_NAME_FLAG_MASK) != 0;
  }

  public boolean isImport() {
    return (flags & IMPORT_FLAG_MASK) != 0;
  }

  public boolean isSerializable() {
    return (flags & SERIALIZABLE_FLAG_MASK) != 0;
  }

  public boolean isBeforeFieldInit() {
    return (flags & BEFORE_FIELD_INIT_FLAG_MASK) != 0;
  }

  public boolean isRTSpecialName() {
    return (flags & RT_SPECIAL_NAME_FLAG_MASK) != 0;
  }

  public boolean hasSecurity() {
    return (flags & HAS_SECURITY_FLAG_MASK) != 0;
  }

  public boolean isTypeForwarder() {
    return (flags & IS_TYPE_FORWARDER_FLAG_MASK) != 0;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof NamedTypeSymbol other) {
      if (!other.getName().equals(getName()) || !other.getNamespace().equals(getNamespace()))
        return false;

      if (other.getTypeArguments().length != getTypeArguments().length) return false;

      for (int i = 0; i < other.getTypeArguments().length; i++) {
        if (other.getTypeArguments()[i].equals(getTypeArguments()[i])) return false;
      }

      return true;
    }
    return super.equals(obj);
  }

  @Override
  public Symbol getType() {
    return NamedTypeSymbol.this;
  }

  // endregion

  public StaticShape<StaticObject.StaticObjectFactory> getShape(boolean isStatic) {
    if (isStatic && staticShape == null || !isStatic && instanceShape == null) {
      createShapes();
    }

    return isStatic ? staticShape : instanceShape;
  }

  public StaticField[] getInstanceFields() {
    if (instanceShape == null) {
      createShapes();
    }

    return instanceFields;
  }

  public StaticField[] getStaticFields() {
    if (staticFields == null) {
      createShapes();
    }

    return staticFields;
  }

  // TODO
  public void safelyInitialize() {}

  private void createShapes() {
    // TODO: Is this invalidation necessary when initializing CompilationFinal fields?
    CompilerDirectives.transferToInterpreterAndInvalidate();

    LinkedFieldLayout layout = new LinkedFieldLayout(getContext(), this, superClass);
    instanceShape = layout.instanceShape;
    staticShape = layout.staticShape;
    instanceFields = layout.instanceFields;
    staticFields = layout.staticFields;
  }

  private static class LazyFactory {
    private static MethodSymbol[] createMethods(NamedTypeSymbol symbol, CLITypeDefTableRow row) {
      var methodRange = CLIFileUtils.getMethodRange(symbol.definingModule.getDefiningFile(), row);

      var methodRow =
          symbol
              .definingModule
              .getDefiningFile()
              .getTableHeads()
              .getMethodDefTableHead()
              .skip(new CLITablePtr(CLITableConstants.CLI_TABLE_METHOD_DEF, methodRange.getLeft()));

      var methods = new MethodSymbol[methodRange.getRight() - methodRange.getLeft()];
      while (methodRow.getRowNo() < methodRange.getRight()) {
        methods[methodRow.getRowNo() - methodRange.getLeft()] =
            symbol.getDefiningModule().getLocalMethod(methodRow.getRowNo());
        methodRow = methodRow.skip(1);
      }

      return methods;
    }

    private static NamedTypeSymbol[] createInterfaces(NamedTypeSymbol symbol) {
      List<NamedTypeSymbol> interfaces = new ArrayList<>();
      for (var interfaceRow :
          symbol.definingModule.getDefiningFile().getTableHeads().getInterfaceImplTableHead()) {
        if (interfaceExtendsClass(
            interfaceRow, symbol.name, symbol.namespace, symbol.definingModule)) {
          interfaces.add(getInterface(interfaceRow, symbol.definingModule, symbol));
        }
      }

      return interfaces.toArray(new NamedTypeSymbol[0]);
    }

    static boolean interfaceExtendsClass(
        CLIInterfaceImplTableRow interfaceRow,
        String extendingClassName,
        String extendingClassNamespace,
        ModuleSymbol module) {
      var potentialExtendingClassRow =
          module
              .getDefiningFile()
              .getTableHeads()
              .getTypeDefTableHead()
              .skip(interfaceRow.getKlassTablePtr());
      // we can not create the whole klass because of circular dependency, we only need the name and
      // namespace
      var potentialClassName =
          potentialExtendingClassRow
              .getTypeNameHeapPtr()
              .read(module.getDefiningFile().getStringHeap());
      var potentialClassNamespace =
          potentialExtendingClassRow
              .getTypeNamespaceHeapPtr()
              .read(module.getDefiningFile().getStringHeap());

      return extendingClassName.equals(potentialClassName)
          && extendingClassNamespace.equals(potentialClassNamespace);
    }

    static NamedTypeSymbol getInterface(
        CLIInterfaceImplTableRow row, ModuleSymbol module, NamedTypeSymbol symbol) {
      CLITablePtr tablePtr = row.getInterfaceTablePtr();
      assert tablePtr != null; // Should never should be
      return (NamedTypeSymbol)
          TypeSymbol.TypeSymbolFactory.create(
              tablePtr, new TypeSymbol[0], symbol.getTypeArguments(), module);
    }

    public static NamedTypeSymbol createDirectBaseClass(NamedTypeSymbol namedTypeSymbol) {
      CLITablePtr baseClassPtr =
          namedTypeSymbol
              .definingModule
              .getDefiningFile()
              .getTableHeads()
              .getTypeDefTableHead()
              .skip(namedTypeSymbol.definingRow)
              .getExtendsTablePtr();

      assert baseClassPtr != null; // Never should be null
      return baseClassPtr.isEmpty()
          ? null
          : (NamedTypeSymbol)
              TypeSymbol.TypeSymbolFactory.create(
                  baseClassPtr,
                  new TypeSymbol[0],
                  namedTypeSymbol.getTypeArguments(),
                  namedTypeSymbol.definingModule);
    }

    public static FieldSymbol[] createFields(NamedTypeSymbol namedTypeSymbol) {
      var row =
          namedTypeSymbol
              .definingModule
              .getDefiningFile()
              .getTableHeads()
              .getTypeDefTableHead()
              .skip(namedTypeSymbol.definingRow);
      var fieldTablePtr = row.getFieldListTablePtr();
      var fieldListEndPtr = row.skip(1).getFieldListTablePtr();

      var fieldRow =
          namedTypeSymbol
              .definingModule
              .getDefiningFile()
              .getTableHeads()
              .getFieldTableHead()
              .skip(fieldTablePtr);

      var fields = new ArrayList<FieldSymbol>();
      while (fieldRow.getRowNo() < fieldListEndPtr.getRowNo() && fieldRow.hasNext()) {
        var field =
            FieldSymbol.FieldSymbolFactory.create(
                fieldRow,
                new TypeSymbol[0],
                namedTypeSymbol.getTypeArguments(),
                namedTypeSymbol.getDefiningModule());

        fields.add(field);
        fieldRow = fieldRow.next();
      }

      return fields.toArray(new FieldSymbol[0]);
    }
  }

  public static class NamedTypeSymbolFactory {
    public static NamedTypeSymbol create(CLITypeRefTableRow row, ModuleSymbol module) {
      var name = row.getTypeNameHeapPtr().read(module.getDefiningFile().getStringHeap());
      var namespace = row.getTypeNamespaceHeapPtr().read(module.getDefiningFile().getStringHeap());

      var resolutionScopeTablePtr = row.getResolutionScopeTablePtr();
      if (resolutionScopeTablePtr == null) {
        // TODO: find in ExportedType table
        throw new NotImplementedException();
      }

      return switch (resolutionScopeTablePtr.getTableId()) {
        case CLITableConstants.CLI_TABLE_TYPE_REF -> throw new UnsupportedOperationException(
            CILOSTAZOLBundle.message("cilostazol.exception.typeRefResolutionScope"));
        case CLITableConstants.CLI_TABLE_MODULE_REF -> getTypeFromDifferentModule(
            name, namespace, resolutionScopeTablePtr, module);
        case CLITableConstants.CLI_TABLE_MODULE -> throw new InvalidCLIException();
        case CLITableConstants.CLI_TABLE_ASSEMBLY_REF -> getTypeFromDifferentAssembly(
            name, namespace, resolutionScopeTablePtr, module);
        default -> throw new TypeSystemException(
            CILOSTAZOLBundle.message(
                "cilostazol.exception.unknownResolutionScope",
                namespace,
                name,
                resolutionScopeTablePtr.getTableId()));
      };
    }

    private static NamedTypeSymbol getTypeFromDifferentModule(
        String name, String namespace, CLITablePtr resolutionScopeTablePtr, ModuleSymbol module) {
      var moduleRefName =
          module
              .getDefiningFile()
              .getTableHeads()
              .getModuleRefTableHead()
              .skip(resolutionScopeTablePtr)
              .getNameHeapPtr()
              .read(module.getDefiningFile().getStringHeap());

      // We can omit looking for file since we know that it is in the same assembly as this module
      // TODO: can be improved by calling getLocalTypeFromModule that would filter modules right
      // away by name
      return module
          .getContext()
          .getType(name, namespace, module.getDefiningFile().getAssemblyIdentity());
    }

    private static NamedTypeSymbol getTypeFromDifferentAssembly(
        String name, String namespace, CLITablePtr resolutionScopeTablePtr, ModuleSymbol module) {
      var referencedAssemblyIdentity =
          AssemblyIdentity.fromAssemblyRefRow(
              module.getDefiningFile().getStringHeap(),
              module
                  .getDefiningFile()
                  .getTableHeads()
                  .getAssemblyRefTableHead()
                  .skip(resolutionScopeTablePtr));
      var referencedAssembly = module.getContext().findAssembly(referencedAssemblyIdentity);
      if (referencedAssembly == null) {
        // TODO: log CILOSTAZOLBundle.message("cilostazol.warning.referencedAssemblyNotFound",
        // referencedAssemblyIdentity.toString())
        return null;
      }
      return referencedAssembly.getLocalType(name, namespace);
    }

    public static NamedTypeSymbol create(CLITypeDefTableRow row, ModuleSymbol module) {
      var nameAndNamespace = CLIFileUtils.getNameAndNamespace(module.getDefiningFile(), row);

      final String name = nameAndNamespace.getLeft();
      final String namespace = nameAndNamespace.getRight();
      final TypeParameterSymbol[] typeParams =
          TypeParameterSymbol.TypeParameterSymbolFactory.create(
              row.getPtr(), new TypeSymbol[0], module);
      int flags = row.getFlags();

      return new NamedTypeSymbol(module, flags, name, namespace, typeParams, row.getPtr());
    }

    public static NamedTypeSymbol create(
        TypeSpecSig typeSig, TypeSymbol[] mvars, TypeSymbol[] vars, ModuleSymbol module) {
      // TODO: null reference exception might have occured here if TypeSig is not created from CLASS
      // TODO: resolve for other types (SZARRAY, GENERICINST, ...)
      if (typeSig.getFlag().getFlag() == ElementTypeFlag.Flag.ELEMENT_TYPE_GENERICINST) {
        NamedTypeSymbol genType =
            (NamedTypeSymbol)
                TypeSymbol.TypeSymbolFactory.create(typeSig.getGenType(), mvars, vars, module);
        TypeSymbol[] typeArgs = new NamedTypeSymbol[typeSig.getTypeArgs().length];
        for (int i = 0; i < typeArgs.length; i++) {
          typeArgs[i] =
              TypeSymbol.TypeSymbolFactory.create(typeSig.getTypeArgs()[i], mvars, vars, module);
        }
        return genType.construct(typeArgs); // TODO: is this correct? @Tomas
      } else if (typeSig.getFlag().getFlag() == ElementTypeFlag.Flag.ELEMENT_TYPE_MVAR) {
        return null;
      } else if (typeSig.getFlag().getFlag() == ElementTypeFlag.Flag.ELEMENT_TYPE_VAR) {
        return null;
      } else {
        return null;
      }
    }
  }
}
