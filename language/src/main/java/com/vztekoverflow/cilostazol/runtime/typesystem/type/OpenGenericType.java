package com.vztekoverflow.cilostazol.runtime.typesystem.type;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cil.parser.cli.table.CLITableRow;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitution;

public class OpenGenericType<T extends CLITableRow<T>> extends TypeBase<T> {
    protected final IType[] _typeParameters;

    public OpenGenericType(T row,
                           CLIFile _definingFile,
                           String _name,
                           String _namespace,
                           IType _directBaseClass,
                           IType[] _interfaces,
                           IComponent _definingComponent,
                           IType[] _typeParameters) {
        super(row, _definingFile, _name, _namespace, _directBaseClass, _interfaces, _definingComponent);
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

    @Override
    public IType getConstructedFrom() {
        return this;
    }
    //endregion
}
