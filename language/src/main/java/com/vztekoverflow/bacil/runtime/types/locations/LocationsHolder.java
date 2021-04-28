package com.vztekoverflow.bacil.runtime.types.locations;

import com.vztekoverflow.bacil.runtime.types.Type;

public final class LocationsHolder {

    /*@CompilerDirectives.CompilationFinal
    private final LocationsDescriptor descriptor;*/

    private final Object[] refs;
    private final long[] primitives;

    public LocationsHolder(int refCount, int primitiveCount) {
        /*CompilerAsserts.partialEvaluationConstant(descriptor);
        this.descriptor = descriptor;
        CompilerAsserts.partialEvaluationConstant(this.descriptor);*/
        //CompilerAsserts.partialEvaluationConstant(this.descriptor);
        refs = new Object[refCount];
        primitives = new long[primitiveCount];
    }

    public static LocationsHolder forDescriptor(LocationsDescriptor descriptor) {
        return new LocationsHolder(descriptor.getRefCount(), descriptor.getPrimitiveCount());
    }

    public static LocationsHolder forArray(Type type, int count)
    {
        if(type.isPrimitiveStorage())
        {
            return new LocationsHolder(0, count);
        } else {
            return new LocationsHolder(count, 0);
        }
    }


    public Object[] getRefs() {
        return refs;
    }

    public long[] getPrimitives() {
        return primitives;
    }

    /*public void locationToStack(int locationIndex, Object[] refs, long[] primitives, int slot)
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
    }*/
}
