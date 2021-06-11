package com.vztekoverflow.bacil.runtime.locations;

import com.vztekoverflow.bacil.runtime.types.Type;

/**
 * Class holding values stored in locations. The uses are described in I.12.1.6.1 Homes for values.
 */
public final class LocationsHolder {

    private final Object[] refs;
    private final long[] primitives;

    /**
     * Create a new locations holder with the specified amount of references and primitives.
     * @param refCount amount of references to store
     * @param primitiveCount amount of primitives to store
     */
    private LocationsHolder(int refCount, int primitiveCount) {
        refs = new Object[refCount];
        primitives = new long[primitiveCount];
    }

    /**
     * Create a new locations holder holding values specified by the descriptor.
     * @param descriptor the locations descriptor for which to hold the values
     */
    public static LocationsHolder forDescriptor(LocationsDescriptor descriptor) {
        return new LocationsHolder(descriptor.getRefCount(), descriptor.getPrimitiveCount());
    }

    /**
     * Create a new locations holder holding elements of an array.
     * @param type the type of the element
     * @param count array size
     */
    public static LocationsHolder forArray(Type type, int count)
    {
        if(type.isPrimitiveStorage())
        {
            return new LocationsHolder(0, count);
        } else {
            return new LocationsHolder(count, 0);
        }
    }


    /**
     * Get the array of stored references.
     */
    public Object[] getRefs() {
        return refs;
    }

    /**
     * Get the array of stored primitives.
     */
    public long[] getPrimitives() {
        return primitives;
    }
}
