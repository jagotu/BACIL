package com.vztekoverflow.cilostazol.runtime.symbols;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cil.parser.cli.signature.ElementTypeFlag;
import com.vztekoverflow.cil.parser.cli.signature.SignatureReader;
import com.vztekoverflow.cil.parser.cli.signature.TypeSig;
import com.vztekoverflow.cil.parser.cli.signature.TypeSpecSig;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.generated.*;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.exceptions.InvalidCLIException;
import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;
import com.vztekoverflow.cilostazol.runtime.symbols.utils.NamedTypeSymbolLayout;
import com.vztekoverflow.cilostazol.runtime.symbols.utils.NamedTypeSymbolSemantics;
import com.vztekoverflow.cilostazol.runtime.symbols.utils.NamedTypeSymbolVisibility;
import com.vztekoverflow.cilostazol.runtime.typesystem.TypeSystemException;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.TypeLayout;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.TypeSemantics;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.TypeVisibility;
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

  @CompilerDirectives.CompilationFinal protected NamedTypeSymbol lazyDirectBaseClass;
  @CompilerDirectives.CompilationFinal protected NamedTypeSymbol[] lazyInterfaces;
  @CompilerDirectives.CompilationFinal protected MethodSymbol[] lazyMethods;
  @CompilerDirectives.CompilationFinal protected MethodSymbol[] lazyVMethodTable;
  @CompilerDirectives.CompilationFinal protected FieldSymbol[] lazyFields;
  protected final TypeParameterSymbol[] typeParameters;
  protected final TypeMap map;
  protected final CLITablePtr definingRow;

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
    super(definingModule);
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
  // endregion

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
    return NamedTypeSymbolVisibility.fromFlags(flags & TypeVisibility.MASK);
  }

  public NamedTypeSymbolLayout getLayout() {
    return NamedTypeSymbolLayout.fromFlags(flags & TypeLayout.MASK);
  }

  public NamedTypeSymbolSemantics getSemantics() {
    return NamedTypeSymbolSemantics.fromFlags(flags & TypeSemantics.MASK);
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
  // endregion

  private static class LazyFactory {
    private static MethodSymbol[] createMethods(NamedTypeSymbol symbol, CLITypeDefTableRow row) {
      var methodTablePtr = row.getMethodListTablePtr();

      final boolean isLastType =
          row.getRowNo()
              == symbol
                  .definingModule
                  .getDefiningFile()
                  .getTablesHeader()
                  .getRowCount(CLITableConstants.CLI_TABLE_TYPE_DEF);
      final int lastIdx =
          isLastType
              ? symbol
                  .definingModule
                  .getDefiningFile()
                  .getTablesHeader()
                  .getRowCount(CLITableConstants.CLI_TABLE_METHOD_DEF)
              : row.skip(1).getMethodListTablePtr().getRowNo();

      var methodRow =
          symbol
              .definingModule
              .getDefiningFile()
              .getTableHeads()
              .getMethodDefTableHead()
              .skip(methodTablePtr);

      var methods = new ArrayList<MethodSymbol>();
      while (methodRow.getRowNo() < lastIdx) {
        var method = MethodSymbol.MethodSymbolFactory.create(methodRow, symbol);
        methods.add(method);
        methodRow = methodRow.next();
      }

      return methods.toArray(MethodSymbol[]::new);
    }

    private static NamedTypeSymbol[] createInterfaces(NamedTypeSymbol symbol) {
      List<NamedTypeSymbol> interfaces = new ArrayList<>();
      for (var interfaceRow :
          symbol.definingModule.getDefiningFile().getTableHeads().getInterfaceImplTableHead()) {
        if (interfaceExtendsClass(
            interfaceRow, symbol.name, symbol.namespace, symbol.definingModule)) {
          interfaces.add(getInterface(interfaceRow, symbol.definingModule));
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

    static NamedTypeSymbol getInterface(CLIInterfaceImplTableRow row, ModuleSymbol module) {
      CLITablePtr tablePtr = row.getInterfaceTablePtr();
      assert tablePtr != null; // Should never should be
      return NamedTypeSymbolFactory.create(tablePtr, new TypeSymbol[0], new TypeSymbol[0], module);
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
          : NamedTypeSymbolFactory.create(
              baseClassPtr, new TypeSymbol[0], new TypeSymbol[0], namedTypeSymbol.definingModule);
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
                namedTypeSymbol.getTypeParameters(),
                namedTypeSymbol.getDefiningModule());

        fields.add(field);
        fieldRow = fieldRow.next();
      }

      return fields.toArray(new FieldSymbol[0]);
    }
  }

  public static class NamedTypeSymbolFactory {
    public static NamedTypeSymbol create(
        CLITablePtr ptr, TypeSymbol[] mvars, TypeSymbol[] vars, ModuleSymbol module) {
      return switch (ptr.getTableId()) {
        case CLITableConstants.CLI_TABLE_TYPE_DEF -> create(
            module.getDefiningFile().getTableHeads().getTypeDefTableHead().skip(ptr), module);
        case CLITableConstants.CLI_TABLE_TYPE_REF -> create(
            module.getDefiningFile().getTableHeads().getTypeRefTableHead().skip(ptr), module);
        case CLITableConstants.CLI_TABLE_TYPE_SPEC -> create(
            module.getDefiningFile().getTableHeads().getTypeSpecTableHead().skip(ptr),
            mvars,
            vars,
            module);
        default -> throw new TypeSystemException(
            CILOSTAZOLBundle.message("cilostazol.exception.constructor.withoutDefType"));
      };
    }

    public static NamedTypeSymbol create(
        CLITypeSpecTableRow row, TypeSymbol[] mvars, TypeSymbol[] vars, ModuleSymbol module) {
      TypeSig signature =
          TypeSig.read(
              new SignatureReader(
                  row.getSignatureHeapPtr().read(module.getDefiningFile().getBlobHeap())),
              module.getDefiningFile());
      return (NamedTypeSymbol) TypeSymbol.TypeSymbolFactory.create(signature, mvars, vars, module);
    }

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
      final String name = row.getTypeNameHeapPtr().read(module.getDefiningFile().getStringHeap());
      final String namespace =
          row.getTypeNamespaceHeapPtr().read(module.getDefiningFile().getStringHeap());
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
        NamedTypeSymbol genType = create(typeSig.getGenType(), mvars, vars, module);
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
