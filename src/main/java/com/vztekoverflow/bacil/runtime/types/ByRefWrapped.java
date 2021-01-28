package com.vztekoverflow.bacil.runtime.types;

import com.vztekoverflow.bacil.runtime.ManagedReference;

public class ByRefWrapped extends Type {

    private final Type inner;

    public ByRefWrapped(Type inner) {
        this.inner = inner;
    }

    @Override
    public byte getTypeCategory() {
        return Type.ELEMENT_TYPE_BYREF;
    }

    public Type getInner() {
        return inner;
    }

    @Override
    public Object fromStackVar(Object ref, long primitive) {
        return ref;
    }

    @Override
    public void toStackVar(Object[] refs, long[] primitives, int slot, Object value) {
        refs[slot] = value;
    }
}
