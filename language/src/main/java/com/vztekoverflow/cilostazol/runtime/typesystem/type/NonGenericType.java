package com.vztekoverflow.cilostazol.runtime.typesystem.type;

import com.vztekoverflow.cilostazol.runtime.typesystem.type.generic.TypeRef;

public class NonGenericType extends Type implements TypeRef {
    @Override
    public Type getType() {
        return this;
    }
}
