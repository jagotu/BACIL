package com.vztekoverflow.cilostazol.runtime.symbols;

import com.vztekoverflow.cilostazol.runtime.context.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.context.ContextProvider;

public abstract class Symbol {
  private final ContextProvider ctxProvider;

  public Symbol(ContextProvider ctxProvider) {
    this.ctxProvider = ctxProvider;
  }

  public CILOSTAZOLContext getContext() {
    return ctxProvider.getContext();
  }

  public Symbol getType() {
    return Symbol.this;
  }
}
