package com.vztekoverflow.bacil.runtime;

import com.vztekoverflow.bacil.runtime.types.locations.LocationsHolder;

public class LocationReference {

    private final LocationsHolder holder;
    private final int holderOffset;

    public LocationReference(LocationsHolder holder, int holderOffset) {
        this.holder = holder;
        this.holderOffset = holderOffset;
    }

    public LocationsHolder getHolder() {
        return holder;
    }

    public int getHolderOffset() {
        return holderOffset;
    }
}
