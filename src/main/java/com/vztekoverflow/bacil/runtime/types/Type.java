package com.vztekoverflow.bacil.runtime.types;

import com.vztekoverflow.bacil.runtime.BACILMethod;
import com.vztekoverflow.bacil.runtime.types.locations.LocationsHolder;

import java.util.List;

public abstract class Type {

    public Object initialValue()
    {
        return null;
    }

    public void stackToLocation(LocationsHolder holder, int holderOffset, Object ref, long primitive)
    {
        holder.getRefs()[holderOffset] = ref;
    }

    public void locationToStack(LocationsHolder holder, int holderOffset, Object[] refs, long[] primitives, int slot)
    {
        refs[slot] = holder.getRefs()[holderOffset];
    }

    public Object stackToObject(Object ref, long primitive)
    {
        return ref;
    }

    public void objectToStack(Object[] refs, long[] primitives, int slot, Object value)
    {
        refs[slot] = value;
    }

    public Object locationToObject(LocationsHolder holder, int holderOffset)
    {
        return holder.getRefs()[holderOffset];
    }

    public void objectToLocation(LocationsHolder holder, int holderOffset, Object obj)
    {
        holder.getRefs()[holderOffset] = obj;
    }

    public abstract Type getDirectBaseClass();

    public abstract BACILMethod getMemberMethod(String name, byte[] signature);

    public abstract boolean isByRef();

    public abstract boolean isPinned();

    public abstract List<CustomMod> getMods();

    public boolean isPrimitiveStorage()
    {
        return false;
    }

}
