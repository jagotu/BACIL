package com.vztekoverflow.cilostazol.runtime.typesystem.method;

import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public interface IParameter extends IType {
    public boolean isByRef();
    public boolean isPinned();
}
