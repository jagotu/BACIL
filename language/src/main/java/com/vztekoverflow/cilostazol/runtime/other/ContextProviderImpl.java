package com.vztekoverflow.cilostazol.runtime.other;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;

import java.util.function.Supplier;

public class ContextProviderImpl implements ContextProvider {
  private static ContextProviderImpl instance;
  @CompilerDirectives.CompilationFinal private Supplier<CILOSTAZOLContext> ctx;

  private ContextProviderImpl() {
    ctx = () -> CILOSTAZOLContext.CONTEXT_REF.get(null);
  }

  public static synchronized ContextProviderImpl getInstance() {
    if (instance == null) instance = new ContextProviderImpl();

    return instance;
  }

  @Override
  public CILOSTAZOLContext getContext() {
    return ctx.get();
  }

  public void setContext(Supplier<CILOSTAZOLContext> ctx) {
    this.ctx = ctx;
  }
}
