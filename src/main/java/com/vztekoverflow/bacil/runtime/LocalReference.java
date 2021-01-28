package com.vztekoverflow.bacil.runtime;

import com.vztekoverflow.bacil.runtime.types.Type;

public class LocalReference implements ManagedReference {
    private final Object[] locals;
    private final int slot;
    private final Type type;

    public LocalReference(Object[] locals, int slot, Type type) {
        this.locals = locals;
        this.slot = slot;
        this.type = type;
    }

    @Override
    public Object getValue()
    {
        return locals[slot];
    }

    @Override
    public void setValue(Object object)
    {
        locals[slot] = object;
    }

    @Override
    public Type getType() {
        return type;
    }
}
