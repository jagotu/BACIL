package com.vztekoverflow.cilostazol.runtime.symbols;

import com.vztekoverflow.cilostazol.runtime.other.ContextProviderImpl;

public class ParameterSymbol extends Symbol {
    public ParameterSymbol() {
        super(ContextProviderImpl.getInstance());
    }
}
