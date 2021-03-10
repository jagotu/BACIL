package com.vztekoverflow.bacil.runtime;

import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.locations.LocationsHolder;

public class SZArray {

    private final Type elementType;
    //private final Object[] fields;
    private final LocationsHolder fieldsHolder;


    public SZArray(Type elementType, int count) {
        this.elementType = elementType;
        this.fieldsHolder = LocationsHolder.forArray(elementType, count);
    }

    public Type getElementType() {
        return elementType;
    }

    public LocationsHolder getFieldsHolder() {
        return fieldsHolder;
    }
}
