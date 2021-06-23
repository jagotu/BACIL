package com.vztekoverflow.bacil.runtime;

import com.vztekoverflow.bacil.runtime.locations.LocationsHolder;
import com.vztekoverflow.bacil.runtime.types.CLIType;

/**
 * An instance of a type, holding instance field values.
 */
public final class StaticObject {

    private final CLIType type;
    private final LocationsHolder fieldsHolder;



    public StaticObject(CLIType type) {
        type.init();
        this.type = type;
        this.fieldsHolder = LocationsHolder.forDescriptor(type.getInstanceFieldsDescriptor());
    }

    public LocationsHolder getFieldsHolder() {
        return fieldsHolder;
    }

    public CLIType getType() {
        return type;
    }

}


