package com.vztekoverflow.cilostazol.runtime.typesystem.method;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.runtime.typesystem.TypeSystemException;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.field.IField;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitution;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ITypeParameter;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public class TypeParameter implements ITypeParameter {
    protected final IType[] _constrains;
    protected final CLIFile _file;
    protected final IComponent _component;

    public TypeParameter(IType[] _constrains, CLIFile _file, IComponent _component) {
        this._constrains = _constrains;
        this._file = _file;
        this._component = _component;
    }

    //region ITypeParameter
    @Override
    public IType[] getConstrains() {
        return _constrains;
    }

    @Override
    public CLIFile getDefiningFile() {
        return _file;
    }

    @Override
    public IType[] getTypeParameters() {
        throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.invalidOperation"));
    }

    @Override
    public String getName() {
        throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.invalidOperation"));
    }

    @Override
    public String getNamespace() {
        throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.invalidOperation"));
    }

    @Override
    public IType getDirectBaseClass() {
        throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.invalidOperation"));
    }

    @Override
    public IType[] getInterfaces() {
        throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.invalidOperation"));
    }

    @Override
    public IMethod[] getMethods() {
        throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.invalidOperation"));
    }

    @Override
    public IMethod[] getVTable() {
        throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.invalidOperation"));
    }

    @Override
    public IField[] getFields() {
        throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.invalidOperation"));
    }

    @Override
    public IComponent getDefiningComponent() {
        return _component;
    }

    @Override
    public IType substitute(ISubstitution<IType> substitution) {
        //TODO: check constrains
        return substitution.substitute(this);
    }

    @Override
    public IType getDefinition() {
        return this;
    }

    @Override
    public IType getConstructedFrom() {
        throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.invalidOperation"));
    }
    //endregion
}
