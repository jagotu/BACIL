package com.vztekoverflow.bacil.runtime.types.locations;

import com.vztekoverflow.bacil.runtime.types.Type;

public abstract class Location {

    protected final Type type;

    protected Location(Type type) {
        this.type = type;
    }

    public abstract void fromStackVar(Object ref, long primitive);

    public abstract void toStackVar(Object[] refs, long[] primitives, int slot);

    public Type getType()
    {
        return type;
    }

    public abstract void fromObject(Object obj);

    public abstract Location clone();
}
