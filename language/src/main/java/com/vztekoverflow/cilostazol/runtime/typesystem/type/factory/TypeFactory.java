package com.vztekoverflow.cilostazol.runtime.typesystem.type.factory;

import com.vztekoverflow.cil.parser.cli.signature.TypeSig;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITableConstants;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeDefTableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeRefTableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeSpecTableRow;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.runtime.typesystem.TypeSystemException;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.CLIComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.NonGenericType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.OpenGenericType;

public final class TypeFactory {
    public static IType create(CLITablePtr ptr, IType[] mvars, IType[] vars, IComponent component) {
        return switch (ptr.getTableId()) {
            case CLITableConstants.CLI_TABLE_TYPE_DEF ->
                    TypeFactory.create(component.getDefiningFile().getTableHeads().getTypeDefTableHead().skip(ptr), component);
            case CLITableConstants.CLI_TABLE_TYPE_REF ->
                    TypeFactory.create(component.getDefiningFile().getTableHeads().getTypeRefTableHead().skip(ptr), component);
            case CLITableConstants.CLI_TABLE_TYPE_SPEC ->
                    TypeFactory.create(component.getDefiningFile().getTableHeads().getTypeSpecTableHead().skip(ptr), mvars, vars, component);
            default ->
                    throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.constructor.withoutDefType"));
        };
    }

    public static IType create(CLITypeDefTableRow typeDefRow, IComponent component) {
        //TODO: method type parameters and class type parameters
        String name = component.getNameFrom(typeDefRow);
        String namespace = component.getNamespaceFrom(typeDefRow);
        IType directBaseClass = FactoryUtils.getDirectBaseClass(typeDefRow, component);
        IType[] interfaces = FactoryUtils.getInterfaces(name, namespace, component);
        IType[] genericParameters = FactoryUtils.getTypeParameters(typeDefRow, (CLIComponent) component);
//                component.getTableHeads().getFieldTableHead().skip(type.getFieldList());
//        var methods = component.getTableHeads().getMethodDefTableHead().skip(type.getMethodList());


        if (genericParameters.length == 0) {
            return new NonGenericType<>(
                    typeDefRow,
                    component.getDefiningFile(),
                    name,
                    namespace,
                    directBaseClass,
                    interfaces,
                    component);
        } else {
            return new OpenGenericType<>(
                    typeDefRow,
                    component.getDefiningFile(),
                    name,
                    namespace,
                    directBaseClass,
                    interfaces,
                    component,
                    genericParameters);
        }
    }

    public static IType create(CLITypeSpecTableRow type, IType[] methodTypeParameters, IType[] classTypeParameters, IComponent component) {
        return null;
    }

    public static IType create(CLITypeRefTableRow type, IComponent component) {
        return null;
    }

    public static IType createVoid(IComponent component) {
        return null;
    }

    public static IType createTypedRef(IComponent component) {
        return null;
    }

    public static IType create(TypeSig tSig, IType[] mvars, IType[] vars, IComponent definingComponent) {
        return null;
    }
}
