package com.vztekoverflow.bacil.runtime;

import com.vztekoverflow.bacil.runtime.locations.LocationsHolder;
import com.vztekoverflow.bacil.runtime.types.Type;

/**
 * An instance of a SZArrayType, holding array element values.
 */
public class SZArray {

    private final Type elementType;
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
