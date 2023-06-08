package com.vztekoverflow.cilostazol.runtime.typesystem.method.parameter;

import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitution;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public class Parameter implements IParameter {
    protected final boolean _isByRef;
    protected final IType _type;
    protected final String _name;
    protected final int _idx;
    protected final ParamFlags _flags;

    public Parameter(boolean _isByRef, IType _type, String name, int idx, ParamFlags flags) {
        this._isByRef = _isByRef;
        this._type = _type;
        _name = name;
        _idx = idx;
        _flags = flags;
    }

    //region IParameter
    @Override
    public IParameter substitute(ISubstitution<IType> substitution) {
        return new Parameter(_isByRef, _type.substitute(substitution),_name, _idx, _flags );
    }

    @Override
    public IParameter getDefinition() {
        throw new NotImplementedException();
    }

    @Override
    public IParameter getConstructedFrom() {
        throw new NotImplementedException();
    }

    @Override
    public boolean isByRef() {
        return _isByRef;
    }

    @Override
    public ParamFlags getFlags() {
        return _flags;
    }

    @Override
    public int getIndex() {
        return _idx;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public IType getType() {
        return _type;
    }
    //endregion
}
