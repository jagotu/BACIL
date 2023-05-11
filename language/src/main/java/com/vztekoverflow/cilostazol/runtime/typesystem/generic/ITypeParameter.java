package com.vztekoverflow.cilostazol.runtime.typesystem.generic;

import com.vztekoverflow.cilostazol.runtime.typesystem.method.VarianceType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public interface ITypeParameter extends IType {
    public IType[] getConstrains();
    public boolean hasNewConstraint();
    public boolean hasClassConstraint();
    public boolean hasValueTypeConstraint();
    public VarianceType getVariance();
}
