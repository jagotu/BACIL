package com.vztekoverflow.bacil.runtime.types;

import com.vztekoverflow.bacil.parser.signatures.MethodDefSig;
import com.vztekoverflow.bacil.runtime.BACILMethod;

import java.util.List;

/**
 * Represents a type with modreq and/or modopt constraints, as specified in II.7.1.1 modreq and modopt.
 */
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
    public BACILMethod getMemberMethod(String name, MethodDefSig signature) {
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
