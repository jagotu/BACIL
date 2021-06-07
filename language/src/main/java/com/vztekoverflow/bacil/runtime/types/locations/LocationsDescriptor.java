package com.vztekoverflow.bacil.runtime.types.locations;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.TypedField;

public class LocationsDescriptor {
    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private final Type[] locationTypes;

    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private final int[] offsets;

    private final int primitiveCount;
    private final int refCount;

    public LocationsDescriptor(Type[] locationTypes) {
        this(null, locationTypes);
    }

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



    public static LocationsDescriptor forFields(LocationsDescriptor parent, TypedField[] typedFields)
    {
        Type[] types = new Type[typedFields.length];
        for(int i = 0; i < typedFields.length;i++)
        {
            types[i] = typedFields[i].getType();
        }
        return new LocationsDescriptor(parent, types);
    }

    public static LocationsDescriptor forFields(TypedField[] typedFields) {
        return forFields(null, typedFields);
    }

    public LocationsDescriptor getExtended(TypedField[] typedFields)
    {


        Type[] types = new Type[typedFields.length];
        for(int i = 0; i < typedFields.length;i++)
        {
            types[i] = typedFields[i].getType();
        }
        return new LocationsDescriptor(types);
    }

    public Type getType(int index)
    {
        return locationTypes[index];
    }

    public int getOffset(int index)
    {
        return offsets[index];
    }

    public int getPrimitiveCount() {
        return primitiveCount;
    }

    public int getRefCount() {
        return refCount;
    }

    public void locationToStack(LocationsHolder holder, int locationIndex, Object[] refs, long[] primitives, int slot)
    {
       locationTypes[locationIndex].locationToStack(holder, offsets[locationIndex], refs, primitives, slot);
    }

    public void stackToLocation(LocationsHolder holder, int locationIndex, Object ref, long primitive)
    {
        locationTypes[locationIndex].stackToLocation(holder, offsets[locationIndex], ref, primitive);
    }

    public void objectToLocation(LocationsHolder holder, int locationIndex, Object object)
    {
        locationTypes[locationIndex].objectToLocation(holder, offsets[locationIndex], object);
    }

    public Object locationToObject(LocationsHolder holder, int locationIndex)
    {
        return locationTypes[locationIndex].locationToObject(holder, offsets[locationIndex]);
    }


    public int getSize()
    {
        return locationTypes.length;
    }
}
