package com.vztekoverflow.bacil.runtime.types;

import com.vztekoverflow.bacil.runtime.BACILMethod;

import java.util.List;

public class CustomModWrapped extends Type {

    private final Type inner;
    private final List<CustomMod> mods;

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
        return inner.isPinned();
    }

    @Override
    public List<CustomMod> getMods() {
        return mods;
    }

    public CustomModWrapped(Type inner, List<CustomMod> mods) {
        this.inner = inner;
        this.mods = mods;
    }
}
