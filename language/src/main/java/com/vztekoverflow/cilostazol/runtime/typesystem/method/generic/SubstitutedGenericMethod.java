package com.vztekoverflow.cilostazol.runtime.typesystem.method.generic;

import com.vztekoverflow.cilostazol.runtime.typesystem.method.Method;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.Type;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.generic.OpenGenericType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.generic.TypeParameterSupplier;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.generic.TypeRef;

public class SubstitutedGenericMethod extends Method implements TypeParameterSupplier {
    private final TypeRef[] _typeParameters = null;
    private final OpenGenericType _constructedFrom = null;
    @Override
    public Type getTypeParameter(int Idx) {
        return _typeParameters[Idx].getType();
    }
}
