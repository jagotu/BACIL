package com.vztekoverflow.cilostazol.runtime.typesystem.method.returnType;

import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitutable;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.parameter.ParamFlags;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public interface IReturnType extends ISubstitutable<IReturnType, IType> {
    ParamFlags getFlags();
    boolean isByRef();
    IType getType();
}
