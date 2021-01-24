package com.vztekoverflow.bacil.runtime.types;

public class PinnedWrapped extends Type {
    private final Type inner;

    public PinnedWrapped(Type inner) {
        this.inner = inner;
    }

    @Override
    public byte getTypeCategory() {
        return inner.getTypeCategory();
    }

    public Type getInner() {
        return inner;
    }
}
