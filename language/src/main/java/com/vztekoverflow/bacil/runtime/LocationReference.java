package com.vztekoverflow.bacil.runtime;

import com.vztekoverflow.bacil.runtime.locations.LocationsHolder;
import com.vztekoverflow.bacil.runtime.types.Type;

/**
 * Implements a managed reference (denoted as {@code &} in the standard).
 */
public final class LocationReference {

    private final LocationsHolder holder;
    private final int primitiveOffset;
    private final int refOffset;
    private final Type referencedType;


    public LocationReference(LocationsHolder holder, int primitiveOffset, int refOffset, Type referencedType) {
        this.holder = holder;
        this.primitiveOffset = primitiveOffset;
        this.refOffset = refOffset;
        this.referencedType = referencedType;
    }

    public LocationsHolder getHolder() {
        return holder;
    }

    public Type getReferencedType() {
        return referencedType;
    }

    public int getPrimitiveOffset() {
        return primitiveOffset;
    }

    public int getRefOffset() {
        return refOffset;
    }
}
