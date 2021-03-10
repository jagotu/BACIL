package com.vztekoverflow.bacil.runtime;

import com.vztekoverflow.bacil.runtime.types.NamedType;
import com.vztekoverflow.bacil.runtime.types.locations.LocationsHolder;

public final class StaticObject {

    private final NamedType type;
    //private final Object[] fields;
    private final LocationsHolder fieldsHolder;



    public StaticObject(NamedType type) {
        type.init();
        this.type = type;
        this.fieldsHolder = LocationsHolder.forDescriptor(type.getInstanceFieldsDescriptor());
    }

    public LocationsHolder getFieldsHolder() {
        return fieldsHolder;
    }

    public NamedType getType() {
        return type;
    }

}


