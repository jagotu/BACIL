package com.vztekoverflow.cilostazol.runtime.other;

import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;

public class ContextProviderImpl implements ContextProvider {
    private static ContextProviderImpl instance;

    private ContextProviderImpl() {}

    public static synchronized ContextProviderImpl getInstance()
    {
        if ( instance == null)
            instance = new ContextProviderImpl();

        return instance;
    }

    @Override
    public CILOSTAZOLContext getContext() {
        return CILOSTAZOLContext.CONTEXT_REF.get(null);
    }
}
