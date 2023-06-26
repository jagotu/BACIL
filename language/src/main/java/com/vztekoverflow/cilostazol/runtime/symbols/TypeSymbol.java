package com.vztekoverflow.cilostazol.runtime.symbols;

import com.vztekoverflow.cil.parser.cli.signature.SignatureReader;
import com.vztekoverflow.cil.parser.cli.signature.TypeSig;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeSpecTableRow;
import com.vztekoverflow.cilostazol.runtime.other.ContextProviderImpl;

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
        return NamedTypeSymbol.NamedTypeSymbolFactory.create(
            typeSig.getCliTablePtr(), mvars, vars, module);
      else if (typeSig.getElementType() == TypeSig.ELEMENT_TYPE_VAR) {
        return vars[typeSig.getIndex()];
      } else if (typeSig.getElementType() == TypeSig.ELEMENT_TYPE_MVAR) {
        return mvars[typeSig.getIndex()];
      } else if (typeSig.getElementType() == TypeSig.ELEMENT_TYPE_GENERICINST) {
        var genType =
            NamedTypeSymbol.NamedTypeSymbolFactory.create(
                typeSig.getCliTablePtr(), mvars, vars, module);
        TypeSymbol[] typeArgs = new TypeSymbol[typeSig.getTypeArgs().length];
        for (int i = 0; i < typeArgs.length; i++) {
          typeArgs[i] = create(typeSig.getTypeArgs()[i], mvars, vars, module);
        }
        return genType.construct(typeArgs);
      } else {
        return null;
      }
    }
  }
}
