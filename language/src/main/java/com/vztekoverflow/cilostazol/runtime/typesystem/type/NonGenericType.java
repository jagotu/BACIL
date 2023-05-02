package com.vztekoverflow.cilostazol.runtime.typesystem.type;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;
import com.vztekoverflow.cilostazol.runtime.typesystem.TypeSystemException;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.field.IField;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitution;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.IMethod;

public class NonGenericType implements IType {
    protected final CLIFile _definingFile;
    protected final String _name;
    protected final String _namespace;
    protected final IType _directBaseClass;
    protected final IType[] _interfaces;
    protected final IMethod[] _methods;
    protected final IMethod[] _vMethodTable;
    protected final IField[] _fields;

    public NonGenericType(CLIFile _definingFile, String _name, String _namespace, IType _directBaseClass, IType[] _interfaces, IMethod[] _methods, IMethod[] _vMethodTable, IField[] _fields, IComponent _definingComponent) {
        this._definingFile = _definingFile;
        this._name = _name;
        this._namespace = _namespace;
        this._directBaseClass = _directBaseClass;
        this._interfaces = _interfaces;
        this._methods = _methods;
        this._vMethodTable = _vMethodTable;
        this._fields = _fields;
        this._definingComponent = _definingComponent;
    }

    protected final IComponent _definingComponent;

    //region IType
    @Override
    public CLIFile getDefiningFile() {
        return _definingFile;
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
        return _namespace;
    }

    @Override
    public IType getDirectBaseClass() {

        return _directBaseClass;
    }

    @Override
    public IType[] getInterfaces() {
        return _interfaces;
    }

    @Override
    public IMethod[] getMethods() {
        return _methods;
    }

    @Override
    public IMethod[] getVTable() {
        return _vMethodTable;
    }

    @Override
    public IField[] getFields() {
        return _fields;
    }

    @Override
    public IComponent getDefiningComponent() {
        return _definingComponent;
    }
    @Override
    public IType substitute(ISubstitution<IType> substitution) {
        throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.invalidOperation"));
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
