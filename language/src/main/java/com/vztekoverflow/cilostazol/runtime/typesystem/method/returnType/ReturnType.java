package com.vztekoverflow.cilostazol.runtime.typesystem.method.returnType;

import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitution;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.parameter.ParamFlags;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public class ReturnType implements IReturnType {
    protected final boolean _byRef;
    protected final IType _type;

    public ReturnType(boolean byRef, IType type) {
        _byRef = byRef;
        _type = type;
    }

    //region IReturnType
    @Override
    public boolean isByRef() {
        return _byRef;
    }

    @Override
    public IType getType() {
        return _type;
    }

    @Override
    public IReturnType substitute(ISubstitution<IType> substitution) {
        throw new NotImplementedException();
    }

    @Override
    public IReturnType getDefinition() {
        throw new NotImplementedException();
    }

    @Override
    public IReturnType getConstructedFrom() {
        throw new NotImplementedException();
    }
    //endregion
}
