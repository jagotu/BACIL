package com.vztekoverflow.bacil.runtime.types.locations;

import com.oracle.truffle.api.CompilerAsserts;

public final class LocationsHolder {

    private final LocationsDescriptor descriptor;

    private final Object[] refs;
    private final byte[] primitives;

    public LocationsHolder(LocationsDescriptor descriptor) {
        CompilerAsserts.partialEvaluationConstant(descriptor);
        this.descriptor = descriptor;
        refs = new Object[descriptor.getRefCount()];
        primitives = new byte[descriptor.getBytesSize()];
    }


    public Object[] getRefs() {
        return refs;
    }

    public byte[] getPrimitives() {
        return primitives;
    }

    public void locationToStack(int locationIndex, Object[] refs, long[] primitives, int slot)
    {
        descriptor.locationToStack(this, locationIndex, refs, primitives, slot);
    }

    public void stackToLocation(int locationIndex, Object ref, long primitive)
    {
        descriptor.stackToLocation(this, locationIndex, ref, primitive);
    }

    public Object locationToObject(int locationIndex)
    {
        return descriptor.locationToObject(this, locationIndex);
    }

    public void objectToLocation(int locationIndex, Object obj)
    {
        descriptor.objectToLocation(this, locationIndex, obj);
    }

    public LocationsDescriptor getDescriptor() {
        return descriptor;
    }
}
