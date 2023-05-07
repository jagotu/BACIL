package com.vztekoverflow.cilostazol.runtime.typesystem.type.factory;

import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeDefTableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeRefTableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeSpecTableRow;
import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public final class TypeFactory {
    //region ITypeFactory
    public static IType create(CLITypeDefTableRow type, IType[] methodTypeParameters, IType[] classTypeParameters, IComponent component) {
        throw new NotImplementedException();
    }

    public static IType create(CLITypeSpecTableRow type, IType[] methodTypeParameters, IType[] classTypeParameters, IComponent component) {
        throw new NotImplementedException();
    }

    public static IType create(CLITypeRefTableRow type, IType[] methodTypeParameters, IType[] classTypeParameters, IComponent component) {
        throw new NotImplementedException();
    }

    public static IType createVoid(IComponent component) {
        throw new NotImplementedException();
    }

    public static IType createTypedRef(IComponent component) {
        throw new NotImplementedException();
    }
    //endregion
}
