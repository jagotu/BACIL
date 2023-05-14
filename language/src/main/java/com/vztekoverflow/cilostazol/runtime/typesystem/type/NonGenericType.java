package com.vztekoverflow.cilostazol.runtime.typesystem.type;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cil.parser.cli.table.CLITableRow;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.runtime.typesystem.TypeSystemException;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitution;

public class NonGenericType<T extends CLITableRow<T>> extends TypeBase<T> {

    public NonGenericType(T row, CLIFile _definingFile, String _name, String _namespace, IType _directBaseClass, IType[] _interfaces, IComponent _definingComponent) {
        super(row, _definingFile, _name, _namespace, _directBaseClass, _interfaces, _definingComponent);
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
