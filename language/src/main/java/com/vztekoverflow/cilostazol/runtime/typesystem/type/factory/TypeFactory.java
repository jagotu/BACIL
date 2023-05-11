package com.vztekoverflow.cilostazol.runtime.typesystem.type.factory;

import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.generated.*;
import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.NonGenericType;

import java.util.ArrayList;
import java.util.List;

public final class TypeFactory {
    //region ITypeFactory
    public static IType create(CLITypeDefTableRow typeDefRow, CLIInterfaceImplTableRow interfaceImplTableHead, IType[] methodTypeParameters, IType[] classTypeParameters, IComponent component) {
        //TODO: method type parameters and class type parameters
        String name = component.getTypeName(typeDefRow.getTypeNameHeapPtr());
        String namespace = component.getTypeNamespace(typeDefRow.getTypeNamespaceHeapPtr());
        IType directBaseClass = getDirectBaseClass(typeDefRow, component);
        IType[] interfaces = null;//getInterfaces(name, namespace, interfaceImplTableHead, component);

//        var methods = component.getTableHeads().getMethodDefTableHead().skip(type.getMethodList());
//        var fieldRows = component.getTableHeads().getFieldTableHead().skip(type.getFieldList());


        return new NonGenericType(
                component.getDefiningFile(),
                name,
                namespace,
                directBaseClass,
                interfaces,
                null,
                null,
                null,
                component);
    }

    private static IType[] getInterfaces(String className, String classNamespace, CLIInterfaceImplTableRow interfaceImplTableHead, IComponent component) {
        List<IType> interfaces = new ArrayList<>();
        //TODO: implement case for ref and spec table
        for (var row : interfaceImplTableHead) {
            //we can not create the whole klass because of circular dependency, we only need the name and namespace
            var klassTableRow = component.getTableHeads().getTypeDefTableHead().skip(row.getKlassTablePtr().getRowNo());//TODO: make impl detail - refactor; -1 because of dummy class
            var klassName = component.getTypeName(klassTableRow.getTypeNameHeapPtr());//TODO: make impl detail - refactor
            //TODO: here it goes out of bounds for string heap for some reason
            var klassNamespace = component.getTypeNamespace(klassTableRow.getTypeNamespaceHeapPtr());//TODO: make impl detail - refactor

            if (className.equals(klassName) && classNamespace.equals(klassNamespace)) {
                interfaces.add(getInterface(row, component));
            }
        }
        //TODO: filter out nulls -> can be removed when implemented case for ref and spec table
        return interfaces.stream().filter(x -> false).toArray(IType[]::new);
    }

    private static IType getInterface(CLIInterfaceImplTableRow row, IComponent component) {
        CLITablePtr tablePtr = row.getInterfaceTablePtr();
        return switch (tablePtr.getTableId()) {
            case CLITableConstants.CLI_TABLE_TYPE_DEF -> component.getLocalType(tablePtr.getRowNo());
            case CLITableConstants.CLI_TABLE_TYPE_REF -> null; //TODO: implement case for ref table
            case CLITableConstants.CLI_TABLE_TYPE_SPEC -> null; //TODO: implement case for spec table
            default -> throw new NotImplementedException();
        };
    }

    private static IType getDirectBaseClass(CLITypeDefTableRow typeDefRow, IComponent component) {
        IType extendz = null;
        var extendsTable = typeDefRow.getExtendsTablePtr();
        if (extendsTable.getRowNo() != 0) {
            switch (extendsTable.getTableId()) {
                case CLITableConstants.CLI_TABLE_TYPE_DEF -> extendz = component.getLocalType(extendsTable.getRowNo());
                case CLITableConstants.CLI_TABLE_TYPE_REF -> extendz = null; //TODO: implement case for ref table
                case CLITableConstants.CLI_TABLE_TYPE_SPEC -> extendz = null; //TODO: implement case for spec table
                default -> throw new NotImplementedException();
            }
        }
        return extendz;
    }

    public static IType create(CLITypeSpecTableRow type, IType[] methodTypeParameters, IType[] classTypeParameters, IComponent component) {
        return null;
    }

    public static IType create(CLITypeRefTableRow type, IType[] methodTypeParameters, IType[] classTypeParameters, IComponent component) {
        return null;
    }

    public static IType createVoid(IComponent component) {
        return null;
    }

    public static IType createTypedRef(IComponent component) {
        return null;
    }
    //endregion
}
