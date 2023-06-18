package com.vztekoverflow.cilostazol.runtime.symbols;

import com.vztekoverflow.cil.parser.cli.signature.RetTypeSig;
import com.vztekoverflow.cilostazol.runtime.other.ContextProviderImpl;

public class ReturnSymbol extends Symbol {
  private final boolean byRef;
  private final TypeSymbol type;

  public ReturnSymbol(boolean byRef, TypeSymbol type) {
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

  public static class ReturnSymbolFactory {
    public static ReturnSymbol create(
        RetTypeSig sig, TypeSymbol[] mvars, TypeSymbol[] vars, ModuleSymbol module) {
      return new ReturnSymbol(sig.isByRef(), null);
    }
  }
}
