package com.vztekoverflow.cilostazol.runtime.typesystem.type.generic;

import com.vztekoverflow.cilostazol.runtime.typesystem.type.Type;

public class SubstitutedGenericType extends Type implements TypeParameterSupplier {
    private final TypeRef[] _typeParameters = null;
    private final OpenGenericType _constructedFrom = null;

    @Override
    public Type getTypeParameter(int Idx) {
        return _typeParameters[Idx].getType();
    }
}
