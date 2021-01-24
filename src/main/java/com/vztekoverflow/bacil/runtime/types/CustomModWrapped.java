package com.vztekoverflow.bacil.runtime.types;

import java.util.ArrayList;
import java.util.List;

public class CustomModWrapped extends Type {

    private final Type inner;
    private final List<CustomMod> mods;

    @Override
    public byte getTypeCategory() {
        return inner.getTypeCategory();
    }

    public Type getInner() {
        return inner;
    }

    public List<CustomMod> getMods() {
        return mods;
    }

    public CustomModWrapped(Type inner, List<CustomMod> mods) {
        this.inner = inner;
        this.mods = mods;
    }
}
