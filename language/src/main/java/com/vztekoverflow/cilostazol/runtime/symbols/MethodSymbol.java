package com.vztekoverflow.cilostazol.runtime.symbols;

import com.vztekoverflow.cilostazol.runtime.other.ContextProviderImpl;

public class MethodSymbol extends Symbol {
    public MethodSymbol() {
        super(ContextProviderImpl.getInstance());
    }
}
