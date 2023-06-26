package com.vztekoverflow.cilostazol.runtime.symbols;

import com.vztekoverflow.cil.parser.cli.signature.RetTypeSig;
import com.vztekoverflow.cilostazol.runtime.other.ContextProviderImpl;

public final class ReturnSymbol extends Symbol {
  private final boolean byRef;
  private final TypeSymbol type;

  private ReturnSymbol(boolean byRef, TypeSymbol type) {
    super(ContextProviderImpl.getInstance());
    this.byRef = byRef;
    this.type = type;
  }

  public boolean isByRef() {
    return byRef;
  }

  public TypeSymbol getType() {
    return type;
  }

  public static final class ReturnSymbolFactory {
    public static ReturnSymbol create(
        RetTypeSig sig, TypeSymbol[] mvars, TypeSymbol[] vars, ModuleSymbol module) {
      return new ReturnSymbol(
          sig.isByRef(),
          TypeSymbol.TypeSymbolFactory.create(sig.getTypeSig(), mvars, vars, module));
    }

    public static ReturnSymbol createWith(ReturnSymbol symbol, TypeSymbol type) {
      return new ReturnSymbol(symbol.isByRef(), type);
    }
  }
}
