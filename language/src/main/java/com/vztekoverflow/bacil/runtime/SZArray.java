package com.vztekoverflow.bacil.runtime;

import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.locations.LocationsHolder;

public class SZArray {

    private final Type elementType;
    //private final Object[] fields;
    private final LocationsHolder fieldsHolder;
    private final int length;


    public SZArray(Type elementType, int length) {
        this.elementType = elementType;
        this.fieldsHolder = LocationsHolder.forArray(elementType, length);
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public Type getElementType() {
        return elementType;
    }

    public LocationsHolder getFieldsHolder() {
        return fieldsHolder;
    }
}
