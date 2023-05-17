package com.vztekoverflow.cilostazol.runtime.typesystem.generic;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.runtime.typesystem.TypeSystemException;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.field.IField;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.IMethod;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public class TypeParameter implements ITypeParameter {
    protected final IType[] _typeConstrains;
    protected final GenericParameterFlags _flags;
    protected final int _idx;
    protected final String _name;

    public TypeParameter(IType[] typeConstrains, GenericParameterFlags flags, int idx, String name) {
        _typeConstrains = typeConstrains;
        _flags = flags;
        _idx = idx;
        _name = name;
    }


    //region ITypeParameter

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

    @Override
    public CLIFile getDefiningFile() {
        throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.invalidOperation"));
    }

    @Override
    public IType[] getTypeParameters() {
        throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.invalidOperation"));
    }

    @Override
    public String getName() {
        return _name;
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
        throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.invalidOperation"));
    }

    @Override
    public GenericParameterFlags getFlags() {
        return _flags;
    }

    @Override
    public int getIndex() {
        return _idx;
    }

    @Override
    public IType[] getTypeConstrains() {
        return _typeConstrains;
    }
    //endregion
}
