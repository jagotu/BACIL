package com.vztekoverflow.bacil.runtime;

import com.vztekoverflow.bacil.runtime.types.NamedType;
import com.vztekoverflow.bacil.runtime.types.TypedField;
import com.vztekoverflow.bacil.runtime.types.locations.LocationsHolder;

public class StaticObject {

    private final NamedType type;
    //private final Object[] fields;
    private final LocationsHolder fieldsHolder;


    public StaticObject(NamedType type) {
        type.initFields();
        this.type = type;
        this.fieldsHolder = new LocationsHolder(type.getFieldsDescriptor());
    }



    public void fieldToStackVar(TypedField field, Object[] refs, long[] primitives, int slot)
    {
        fieldsHolder.locationToStack(field.getOffset(), refs, primitives, slot);
    }

    public void fieldFromStackVar(TypedField field, Object ref, long primitive)
    {
        fieldsHolder.stackToLocation(field.getOffset(), ref, primitive);
    }

    public NamedType getType() {
        return type;
    }

}


