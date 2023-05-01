package com.vztekoverflow.cilostazol.runtime.typesystem.type.generic;

import com.vztekoverflow.cilostazol.runtime.typesystem.type.Type;

public class OpenGenericType extends Type implements TypeParameterSupplier {

    public SubstitutedGenericType substitute(TypeRef[] refs) {
        return null;
    }

    @Override
    public Type getTypeParameter(int Idx) {
        return null;
    }
}
