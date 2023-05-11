package com.vztekoverflow.cilostazol.runtime.typesystem.method.parameter;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.field.IField;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitution;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.IMethod;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.parameter.IParameter;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public class Parameter implements IParameter {
    protected final boolean _isByRef;
    protected final boolean _isPinned;
    protected final IType _type;

    public Parameter(boolean _isByRef, boolean _isPinned, IType _type) {
        this._isByRef = _isByRef;
        this._isPinned = _isPinned;
        this._type = _type;
    }

    //region IParameter
    @Override
    public boolean isByRef() {
        return _isByRef;
    }

    @Override
    public boolean isPinned() {
        return _isPinned;
    }

    @Override
    public CLIFile getDefiningFile() {
        return _type.getDefiningFile();
    }

    @Override
    public IType[] getTypeParameters() {
        return _type.getTypeParameters();
    }

    @Override
    public String getName() {
        return _type.getName();
    }

    @Override
    public String getNamespace() {
        return _type.getNamespace();
    }

    @Override
    public IType getDirectBaseClass() {
        return _type.getDirectBaseClass();
    }

    @Override
    public IType[] getInterfaces() {
        return _type.getInterfaces();
    }

    @Override
    public IMethod[] getMethods() {
        return _type.getMethods();
    }

    @Override
    public IMethod[] getVTable() {
        return _type.getVTable();
    }

    @Override
    public IField[] getFields() {
        return _type.getFields();
    }

    @Override
    public IComponent getDefiningComponent() {
        return _type.getDefiningComponent();
    }

    @Override
    public IType substitute(ISubstitution<IType> substitution) {
        return _type.substitute(substitution);
    }

    @Override
    public IType getDefinition() {
        return _type.getDefinition();
    }

    @Override
    public IType getConstructedFrom() {
        return _type.getConstructedFrom();
    }
    //endregion
}
