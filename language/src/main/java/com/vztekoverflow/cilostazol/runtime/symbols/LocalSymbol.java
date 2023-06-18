package com.vztekoverflow.cilostazol.runtime.symbols;

import com.vztekoverflow.cilostazol.runtime.other.ContextProviderImpl;

public class LocalSymbol extends Symbol {
    public LocalSymbol() {
        super(ContextProviderImpl.getInstance());
    }
}
