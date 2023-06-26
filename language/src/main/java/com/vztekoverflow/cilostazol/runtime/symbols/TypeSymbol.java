package com.vztekoverflow.cilostazol.runtime.symbols;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.vztekoverflow.cil.parser.cli.signature.SignatureReader;
import com.vztekoverflow.cil.parser.cli.signature.TypeSig;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITableConstants;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeSpecTableRow;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.meta.SystemTypes;
import com.vztekoverflow.cilostazol.runtime.other.ContextProviderImpl;
import com.vztekoverflow.cilostazol.runtime.typesystem.TypeSystemException;

public abstract class TypeSymbol extends Symbol {
  private final SystemTypes kind;
  protected final ModuleSymbol definingModule;

  public TypeSymbol(ModuleSymbol definingModule) {
    super(ContextProviderImpl.getInstance());
    this.definingModule = definingModule;
    // TODO
    this.kind = SystemTypes.Object;
  }

  public ModuleSymbol getDefiningModule() {
    return definingModule;
  }

  public boolean isInterface() {
    return false;
  }

  public boolean isArray() {
    return false;
  }

  public NamedTypeSymbol[] getInterfaces() {
    return new NamedTypeSymbol[0];
  }

  public NamedTypeSymbol[] getSuperTypes() {
    return new NamedTypeSymbol[0];
  }

  protected int getHierarchyDepth() {
    // TODO
    return 0;
  }

  public SystemTypes getKind() {
    return kind;
  }

  public boolean isAssignableFrom(TypeSymbol other) {
    if (this == other) return true;

    if (this.isArray()) {
      if (other.isArray()) {
        TODO:
        return false; // ((ArrayKlass) this).arrayTypeChecks((ArrayKlass) other);
      }
    }

    if (this.isInterface()) {
      return checkInterfaceSubclassing(other);
    }
    return checkOrdinaryClassSubclassing(other);
  }

  /**
   * Performs type checking for non-interface, non-array classes.
   *
   * @param other the class whose type is to be checked against {@code this}
   * @return true if {@code other} is a subclass of {@code this}
   */
  public boolean checkOrdinaryClassSubclassing(TypeSymbol other) {
    int depth = getHierarchyDepth();
    return other.getHierarchyDepth() >= depth && other.getSuperTypes()[depth] == this;
  }

  /**
   * Performs type checking for interface classes.
   *
   * @param other the class whose type is to be checked against {@code this}
   * @return true if {@code this} is a super interface of {@code other}
   */
  public boolean checkInterfaceSubclassing(TypeSymbol other) {
    // TODO: Are these interfaces transitive?
    NamedTypeSymbol[] interfaces = other.getInterfaces();
    return fastLookup(this, interfaces) >= 0;
  }

  protected static int fastLookup(TypeSymbol target, TypeSymbol[] types) {
    if (!CompilerDirectives.isPartialEvaluationConstant(types)) {
      return fastLookupBoundary(target, types);
    }
    // PE-friendly.
    CompilerAsserts.partialEvaluationConstant(types);
    return fastLookupImpl(target, types);
  }

  @ExplodeLoop(kind = ExplodeLoop.LoopExplosionKind.FULL_EXPLODE_UNTIL_RETURN)
  protected static int fastLookupImpl(TypeSymbol target, TypeSymbol[] types) {
    for (int i = 0; i < types.length; i++) {
      if (types[i].getType() == target) {
        return i;
      }
    }

    return -1;
  }

  @CompilerDirectives.TruffleBoundary(allowInlining = true)
  protected static int fastLookupBoundary(TypeSymbol target, TypeSymbol[] types) {
    return fastLookupImpl(target, types);
  }

  public static final class TypeSymbolFactory {
    public static TypeSymbol create(
        TypeSig typeSig, TypeSymbol[] mvars, TypeSymbol[] vars, ModuleSymbol module) {
      // TODO: null reference exception might have occured here if TypeSig is not created from CLASS
      // TODO: resolve for other types (SZARRAY, GENERICINST, ...)
      if (typeSig.getElementType() == TypeSig.ELEMENT_TYPE_VALUETYPE
          || typeSig.getElementType() == TypeSig.ELEMENT_TYPE_CLASS)
        return create(typeSig.getCliTablePtr(), mvars, vars, module);
      else if (typeSig.getElementType() == TypeSig.ELEMENT_TYPE_VAR) {
        return vars[typeSig.getIndex()];
      } else if (typeSig.getElementType() == TypeSig.ELEMENT_TYPE_MVAR) {
        return mvars[typeSig.getIndex()];
      } else if (typeSig.getElementType() == TypeSig.ELEMENT_TYPE_GENERICINST) {
        var genType = (NamedTypeSymbol) create(typeSig.getCliTablePtr(), mvars, vars, module);
        TypeSymbol[] typeArgs = new TypeSymbol[typeSig.getTypeArgs().length];
        for (int i = 0; i < typeArgs.length; i++) {
          typeArgs[i] = create(typeSig.getTypeArgs()[i], mvars, vars, module);
        }
        return genType.construct(typeArgs);
      } else {
        return null;
      }
    }

    public static TypeSymbol create(
        CLITablePtr ptr, TypeSymbol[] mvars, TypeSymbol[] vars, ModuleSymbol module) {
      return switch (ptr.getTableId()) {
        case CLITableConstants.CLI_TABLE_TYPE_DEF -> NamedTypeSymbol.NamedTypeSymbolFactory.create(
            module.getDefiningFile().getTableHeads().getTypeDefTableHead().skip(ptr), module);
        case CLITableConstants.CLI_TABLE_TYPE_REF -> NamedTypeSymbol.NamedTypeSymbolFactory.create(
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

    public static TypeSymbol create(
        CLITypeSpecTableRow row, TypeSymbol[] mvars, TypeSymbol[] vars, ModuleSymbol module) {
      TypeSig signature =
          TypeSig.read(
              new SignatureReader(
                  row.getSignatureHeapPtr().read(module.getDefiningFile().getBlobHeap())),
              module.getDefiningFile());
      return TypeSymbol.TypeSymbolFactory.create(signature, mvars, vars, module);
    }
  }
}
