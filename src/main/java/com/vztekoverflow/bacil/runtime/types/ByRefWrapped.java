package com.vztekoverflow.bacil.runtime.types;

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
}
