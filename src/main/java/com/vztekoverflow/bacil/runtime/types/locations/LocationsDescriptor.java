package com.vztekoverflow.bacil.runtime.types.locations;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.TypedField;

public class LocationsDescriptor {
    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private final Type[] locationTypes;
    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private final int[] offsets;

    private final int bytesSize;
    private final int refCount;

    public LocationsDescriptor(Type[] locationTypes) {
        this.locationTypes = locationTypes;
        offsets = new int[locationTypes.length];

        int byteOffset = 0;
        int refOffset = 0;

        for(int i = 0; i < locationTypes.length; i++)
        {
            Type t = locationTypes[i];
            if(t.isPrimitiveStorage())
            {
                offsets[i] = byteOffset;
                byteOffset += t.getPrimitiveStorageSize();
            } else {
                offsets[i] = refOffset++;
            }
        }

        bytesSize = byteOffset;
        refCount = refOffset;
    }

    public static LocationsDescriptor forFields(TypedField[] typedFields)
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

    public int getBytesSize() {
        return bytesSize;
    }

    public int getRefCount() {
        return refCount;
    }

    public void locationToStack(LocationsHolder holder, int locationIndex, Object[] refs, long[] primitives, int slot)
    {
       locationTypes[locationIndex].stackToLocation(holder, offsets[locationIndex], refs, primitives, slot);
    }

    public void stackToLocation(LocationsHolder holder, int locationIndex, Object ref, long primitive)
    {
        locationTypes[locationIndex].locationToStack(holder, offsets[locationIndex], ref, primitive);
    }

    public void objectToLocation(LocationsHolder holder, int locationIndex, Object object)
    {
        locationTypes[locationIndex].objectToLocation(holder, offsets[locationIndex], object);
    }

    public Object locationToObject(LocationsHolder holder, int locationIndex)
    {
        return locationTypes[locationIndex].locationToObject(holder, offsets[locationIndex]);
    }
}
