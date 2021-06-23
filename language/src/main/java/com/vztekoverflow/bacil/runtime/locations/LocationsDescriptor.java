package com.vztekoverflow.bacil.runtime.locations;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.TypedField;

/**
 * Describes the shape of a location holder, mapping location indices to primitive and reference storages.
 * The uses of location storage are described in I.12.1.6.1 Homes for values.
 */
public class LocationsDescriptor {
    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private final Type[] locationTypes;

    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private final int[] offsets;

    private final int primitiveCount;
    private final int refCount;

    /**
     * Create a new descriptor for locations of the specified types.
     * @param locationTypes types of the locations
     */
    public LocationsDescriptor(Type[] locationTypes) {
        this(null, locationTypes);
    }

    /**
     * Create a new descriptor for locations of the specified types, inheriting all locations from the parent descriptor.
     * @param parent locations descriptor to inherit fields from
     * @param locationTypes types of additional locations
     */
    private LocationsDescriptor(LocationsDescriptor parent, Type[] locationTypes) {
        if(parent != null)
        {
            this.locationTypes = new Type[parent.locationTypes.length + locationTypes.length];
            System.arraycopy(parent.locationTypes, 0, this.locationTypes, 0, parent.locationTypes.length);
            System.arraycopy(locationTypes, 0, this.locationTypes, parent.locationTypes.length, locationTypes.length);
        } else {
            this.locationTypes = locationTypes;
        }

        offsets = new int[this.locationTypes.length];
        int primitiveOffset = 0;
        int refOffset = 0;
        int start = 0;

        if(parent != null)
        {
            System.arraycopy(parent.offsets, 0, this.offsets, 0, parent.offsets.length);
            primitiveOffset = parent.primitiveCount;
            refOffset = parent.refCount;
            start = parent.locationTypes.length;
        }


        for(int i = 0; i < locationTypes.length; i++)
        {
            Type t = locationTypes[i];
            if(t.isPrimitiveStorage())
            {
                offsets[start+i] = primitiveOffset++;
            } else {
                offsets[start+i] = refOffset++;
            }
        }

        primitiveCount = primitiveOffset;
        refCount = refOffset;
    }


    /**
     * Create a new descriptor for locations representing typed fields, inheriting all locations from the parent descriptor.
     * @param parent locations descriptor to inherit fields from
     * @param typedFields the typed fields to create locations for
     * @return locations descriptor representing the typed fields
     */
    public static LocationsDescriptor forFields(LocationsDescriptor parent, TypedField[] typedFields)
    {
        Type[] types = new Type[typedFields.length];
        for(int i = 0; i < typedFields.length;i++)
        {
            types[i] = typedFields[i].getType();
        }
        return new LocationsDescriptor(parent, types);
    }


    /**
     * Create a new descriptor for locations representing typed fields.
     * @param typedFields the typed fields to create locations for
     * @return locations descriptor representing the typed fields
     */
    public static LocationsDescriptor forFields(TypedField[] typedFields) {
        return forFields(null, typedFields);
    }

    /**
     * Get type of the location at index.
     */
    public Type getType(int index)
    {
        return locationTypes[index];
    }

    /**
     * Get the array offset at which the location at index is stored.
     * This will be an offset into the primitives or references array based on the location type.
     */
    public int getOffset(int index)
    {
        return offsets[index];
    }

    /**
     * Get the count of primitives defined in this location descriptor.
     */
    public int getPrimitiveCount() {
        return primitiveCount;
    }

    /**
     * Get the count of references defined in this location descriptor.
     */
    public int getRefCount() {
        return refCount;
    }

    /**
     * Move a value from a location (in a holder described by this descriptor) to the evaluation stack.
     * @param holder location holder to load from
     * @param locationIndex index of the location to load from
     * @param refs references currently on the evaluation stack
     * @param primitives primitives currently on the evaluation stack
     * @param slot the evaluation stack slot to put the value into
     */
    public void locationToStack(LocationsHolder holder, int locationIndex, Object[] refs, long[] primitives, int slot)
    {
       locationTypes[locationIndex].locationToStack(holder, offsets[locationIndex], refs, primitives, slot);
    }

    /**
     * Move a value from the evalutaion stack to a location (in a holder described by this descriptor).
     * @param holder location holder to store to
     * @param locationIndex index of the location to store to
     * @param ref the reference from the evaluation stack
     * @param primitive the primitive from the evaluation stack
     */
    public void stackToLocation(LocationsHolder holder, int locationIndex, Object ref, long primitive)
    {
        locationTypes[locationIndex].stackToLocation(holder, offsets[locationIndex], ref, primitive);
    }

    /**
     * Put an object holding a value into a location (in a holder described by this descriptor).
     * @param holder location holder to store to
     * @param locationIndex index of the location to store to
     * @param value an object holding the value
     */
    public void objectToLocation(LocationsHolder holder, int locationIndex, Object value)
    {
        locationTypes[locationIndex].objectToLocation(holder, offsets[locationIndex], value);
    }

    /**
     * Get a value from a location (in a holder described by this descriptor) as a Java object.
     * @param holder location holder to load from
     * @param locationIndex index of the location to load from
     * @return the value as a Java object
     */
    public Object locationToObject(LocationsHolder holder, int locationIndex)
    {
        return locationTypes[locationIndex].locationToObject(holder, offsets[locationIndex]);
    }

    /**
     * Get the number of locations in this descriptor.
     */
    public int getSize()
    {
        return locationTypes.length;
    }
}
