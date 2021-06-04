package com.vztekoverflow.bacil.runtime;

import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.locations.LocationsHolder;

/**
 * Implements a managed reference (denoted as {@code &} in the standard).
 */
public class LocationReference {

    private final LocationsHolder holder;
    private final int holderOffset;
    private final Type referencedType;

    public LocationReference(LocationsHolder holder, int holderOffset, Type referencedType) {
        this.holder = holder;
        this.holderOffset = holderOffset;
        this.referencedType = referencedType;
    }

    public LocationsHolder getHolder() {
        return holder;
    }

    public int getHolderOffset() {
        return holderOffset;
    }

    public Type getReferencedType() {
        return referencedType;
    }
}
