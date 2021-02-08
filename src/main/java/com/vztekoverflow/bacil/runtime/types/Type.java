package com.vztekoverflow.bacil.runtime.types;

import com.vztekoverflow.bacil.runtime.BACILMethod;

import java.util.List;

public abstract class Type {

    public Object initialValue()
    {
        return null;
    }

    public Object fromStackVar(Object ref, long primitive)
    {
        return ref;
    }

    public void toStackVar(Object[] refs, long[] primitives, int slot, Object value)
    {
        refs[slot] = value;
    }

    public abstract Type getDirectBaseClass();

    public abstract BACILMethod getMemberMethod(String name, byte[] signature);

    public abstract boolean isByRef();

    public abstract boolean isPinned();

    public abstract List<CustomMod> getMods();


}
