package com.vztekoverflow.cilostazol.runtime.typesystem.type;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.runtime.typesystem.TypeSystemException;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.field.IField;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitution;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.IMethod;

public class NonGenericType extends TypeBase {

    public NonGenericType(CLIFile _definingFile, String _name, String _namespace, IType _directBaseClass, IType[] _interfaces, IMethod[] _methods, IMethod[] _vMethodTable, IField[] _fields, IComponent _definingComponent) {
        super(_definingFile, _name, _namespace, _directBaseClass, _interfaces, _methods, _vMethodTable, _fields, _definingComponent);
    }

    //region TypeBase
    @Override
    public IType[] getTypeParameters() {
        throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.invalidOperation"));
    }

    @Override
    public IType substitute(ISubstitution<IType> substitution) {
        throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.invalidOperation"));
    }

    @Override
    public IType getConstructedFrom() {
        throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.invalidOperation"));
    }
    //endregion
}
