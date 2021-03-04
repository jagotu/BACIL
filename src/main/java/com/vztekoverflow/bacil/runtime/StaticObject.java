package com.vztekoverflow.bacil.runtime;

import com.vztekoverflow.bacil.runtime.types.NamedType;
import com.vztekoverflow.bacil.runtime.types.TypedField;
import com.vztekoverflow.bacil.runtime.types.locations.LocationsHolder;

public class StaticObject {

    private final NamedType type;
    //private final Object[] fields;
    private final LocationsHolder fieldsHolder;



    public StaticObject(NamedType type) {
        type.init();
        this.type = type;
        this.fieldsHolder = new LocationsHolder(type.getInstanceFieldsDescriptor());
    }



    public void fieldToStackVar(TypedField field, Object[] refs, long[] primitives, int slot)
    {
        type.getInstanceFieldsDescriptor().locationToStack(fieldsHolder, field.getOffset(), refs, primitives, slot);
    }

    public void fieldFromStackVar(TypedField field, Object ref, long primitive)
    {
        type.getInstanceFieldsDescriptor().stackToLocation(fieldsHolder, field.getOffset(), ref, primitive);
    }

    public LocationReference getFieldReference(TypedField field)
    {
        return new LocationReference(fieldsHolder, field.getOffset());
    }

    public NamedType getType() {
        return type;
    }

}


