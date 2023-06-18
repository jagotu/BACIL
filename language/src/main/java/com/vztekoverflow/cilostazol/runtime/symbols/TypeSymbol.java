package com.vztekoverflow.cilostazol.runtime.symbols;

import com.vztekoverflow.cilostazol.runtime.other.ContextProviderImpl;

public class TypeSymbol extends Symbol {
  protected final ModuleSymbol definingModule;

  public TypeSymbol(ModuleSymbol definingModule) {
    super(ContextProviderImpl.getInstance());
    this.definingModule = definingModule;
  }

  public ModuleSymbol getDefiningModule() {
    return definingModule;
  }
}
