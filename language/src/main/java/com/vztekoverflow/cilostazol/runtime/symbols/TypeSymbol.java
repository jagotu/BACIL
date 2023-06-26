package com.vztekoverflow.cilostazol.runtime.symbols;

import com.vztekoverflow.cil.parser.cli.signature.SignatureReader;
import com.vztekoverflow.cil.parser.cli.signature.TypeSig;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITableConstants;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeSpecTableRow;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.runtime.other.ContextProviderImpl;
import com.vztekoverflow.cilostazol.runtime.typesystem.TypeSystemException;

public abstract class TypeSymbol extends Symbol {
  protected final ModuleSymbol definingModule;

  public TypeSymbol(ModuleSymbol definingModule) {
    super(ContextProviderImpl.getInstance());
    this.definingModule = definingModule;
  }

  public ModuleSymbol getDefiningModule() {
    return definingModule;
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
