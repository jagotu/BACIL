package com.vztekoverflow.cilostazol.runtime.typesystem.generic;

import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public interface ITypeParameter extends IType {
    public String getName();
    public GenericParameterFlags getFlags();
    public int getIndex();
    public IType[] getTypeConstrains();
}
