package com.vztekoverflow.bacil.runtime.types;

import com.vztekoverflow.bacil.runtime.BACILMethod;

import java.util.List;

public class PinnedWrapped extends Type {
    private final Type inner;

    public PinnedWrapped(Type inner) {
        this.inner = inner;
    }

    public Type getInner() {
        return inner;
    }


    @Override
    public Type getDirectBaseClass() {
        return inner.getDirectBaseClass();
    }

    @Override
    public BACILMethod getMemberMethod(String name, byte[] signature) {
        return inner.getMemberMethod(name, signature);
    }

    @Override
    public boolean isByRef() {
        return inner.isByRef();
    }

    @Override
    public boolean isPinned() {
        return true;
    }

    @Override
    public List<CustomMod> getMods() {
        return inner.getMods();
    }
}
