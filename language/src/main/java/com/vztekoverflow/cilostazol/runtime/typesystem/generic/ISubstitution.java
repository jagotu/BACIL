package com.vztekoverflow.cilostazol.runtime.typesystem.generic;

import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public interface ISubstitution<TItem> {
    public TItem substitute(TItem type);
}
