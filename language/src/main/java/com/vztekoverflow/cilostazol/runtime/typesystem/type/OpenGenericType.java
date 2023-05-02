package com.vztekoverflow.cilostazol.runtime.typesystem.type;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.field.IField;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitution;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.IMethod;

public class OpenGenericType extends NonGenericType {
    protected final IType[] _typeParameters;
    public OpenGenericType(CLIFile _definingFile,
                           String _name,
                           String _namespace,
                           IType _directBaseClass,
                           IType[] _interfaces,
                           IMethod[] _methods,
                           IMethod[] _vMethodTable,
                           IField[] _fields,
                           IComponent _definingComponent,
                           IType[] _typeParameters) {
        super(_definingFile, _name, _namespace, _directBaseClass, _interfaces, _methods, _vMethodTable, _fields, _definingComponent);
        this._typeParameters = _typeParameters;
    }

    //region NonGenericType
    @Override
    public IType[] getTypeParameters() {
        return _typeParameters;
    }

    @Override
    public IType substitute(ISubstitution<IType> substitution) {
        return new SubstitutedGenericType(this, this, substitution);
    }
    //endregion
}
