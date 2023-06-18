package com.vztekoverflow.cilostazol.runtime.symbols;

import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.other.ContextProvider;

public abstract class Symbol {
  private final ContextProvider ctxProvider;

  public Symbol(ContextProvider ctxProvider) {
    this.ctxProvider = ctxProvider;
  }

  public CILOSTAZOLContext getContext() {
    return ctxProvider.getContext();
  }
}
