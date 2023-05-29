package com.vztekoverflow.cilostazol.objectmodel;

import com.vztekoverflow.cilostazol.meta.SystemTypes;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;

import java.lang.reflect.Modifier;

public class ValueKlass extends Klass {

    private final SystemTypes primitiveKind;

    public ValueKlass(CILOSTAZOLContext context, SystemTypes primitiveKind) {
        super(context, Modifier.ABSTRACT | Modifier.FINAL | Modifier.PUBLIC);
        this.primitiveKind = primitiveKind;
        assert getMeta().system_Type != null;
        initializeCilostazolClass();
    }

    @Override
    public Klass getElementalType() {
        return this;
    }

    @Override
    public int getClassModifiers() {
        return getModifiers();
    }
}
