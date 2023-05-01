package com.vztekoverflow.cilostazol.runtime.typesystem.method.generic;

import com.vztekoverflow.cilostazol.runtime.typesystem.method.Method;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.Type;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.generic.TypeParameterSupplier;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.generic.TypeRef;

public class OpenGenericMethod extends Method implements TypeParameterSupplier {
    public SubstitutedGenericMethod substitute(TypeRef[] refs) {
        return null;
    }

    @Override
    public Type getTypeParameter(int Idx) {
        return null;
    }
}
