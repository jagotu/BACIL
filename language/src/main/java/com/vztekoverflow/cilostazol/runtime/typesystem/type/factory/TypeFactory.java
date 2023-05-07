package com.vztekoverflow.cilostazol.runtime.typesystem.type.factory;

import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeDefTableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeRefTableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeSpecTableRow;
import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.NonGenericType;

public final class TypeFactory {
    //region ITypeFactory
    public static IType create(CLITypeDefTableRow type, IType[] methodTypeParameters, IType[] classTypeParameters, IComponent component) {
        //TODO: method type parameters and class type parameters
        String name = type.getTypeName().read(component.getStringHeap());
        String namespace = type.getTypeNamespace().read(component.getStringHeap());
        IType extendz = null; //TODO
//        if(type.getExtends().getRowNo() == 0)
//        {
//            extendz = null;
//        } else {
//            extendz = component.getType(type.getExtends());
//        }
//        var methods = component.getTableHeads().getMethodDefTableHead().skip(type.getMethodList());
//        var fieldRows = component.getTableHeads().getFieldTableHead().skip(type.getFieldList());


        return new NonGenericType(
                component.getDefiningFile(),
                name,
                namespace,
                null,
                null,
                null,
                null,
                null,
                component);
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
