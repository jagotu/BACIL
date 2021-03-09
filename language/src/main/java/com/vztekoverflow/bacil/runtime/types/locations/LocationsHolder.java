package com.vztekoverflow.bacil.runtime.types.locations;

public final class LocationsHolder {

    /*@CompilerDirectives.CompilationFinal
    private final LocationsDescriptor descriptor;*/

    private final Object[] refs;
    private final long[] primitives;

    public LocationsHolder(LocationsDescriptor descriptor) {
        /*CompilerAsserts.partialEvaluationConstant(descriptor);
        this.descriptor = descriptor;
        CompilerAsserts.partialEvaluationConstant(this.descriptor);*/
        //CompilerAsserts.partialEvaluationConstant(this.descriptor);
        refs = new Object[descriptor.getRefCount()];
        primitives = new long[descriptor.getPrimitiveCount()];
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
