package com.vztekoverflow.cilostazol.runtime.typesystem.type.generic;

import com.vztekoverflow.cilostazol.runtime.typesystem.type.Type;

public class GenericParameterRef implements TypeRef {
    private final TypeParameterSupplier _supplier;
    private final int _typeParameterIdx;

    public GenericParameterRef(TypeParameterSupplier supplier, int typeParameterIdx) {
        _supplier = supplier;
        _typeParameterIdx = typeParameterIdx;
    }

    @Override
    public Type getType() {
        return _supplier.getTypeParameter(_typeParameterIdx);
    }
}
