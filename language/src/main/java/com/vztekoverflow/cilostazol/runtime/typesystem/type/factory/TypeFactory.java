package com.vztekoverflow.cilostazol.runtime.typesystem.type.factory;

import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.generated.*;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;
import com.vztekoverflow.cilostazol.runtime.typesystem.TypeSystemException;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.CLIComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.TypeParameter;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.NonGenericType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.OpenGenericType;

import java.util.ArrayList;
import java.util.List;

public final class TypeFactory {
    //region ITypeFactory
    public static IType create(CLITypeDefTableRow typeDefRow, IComponent component) {
        //TODO: method type parameters and class type parameters
        String name = component.getTypeName(typeDefRow);
        String namespace = component.getTypeNamespace(typeDefRow);
        IType directBaseClass = getDirectBaseClass(typeDefRow, component);
        IType[] interfaces = getInterfaces(name, namespace, component);
        IType[] genericParameters = FactoryUtils.getTypeParameters(typeDefRow, (CLIComponent)component);
//        var methods = component.getTableHeads().getMethodDefTableHead().skip(type.getMethodList());
//        var fieldRows = component.getTableHeads().getFieldTableHead().skip(type.getFieldList());


        if (genericParameters.length == 0) {
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
        } else {
            return new OpenGenericType(
                    component.getDefiningFile(),
                    name,
                    namespace,
                    directBaseClass,
                    interfaces,
                    null,
                    null,
                    null,
                    component,
                    genericParameters);
        }
    }

    private static boolean pointToSameRow(CLITablePtr genParRowOwner, CLITablePtr constraintRowOwner) {
        return genParRowOwner.getRowNo() == constraintRowOwner.getRowNo();
    }

    private static IType[] getInterfaces(String className, String classNamespace, IComponent component) {
        List<IType> interfaces = new ArrayList<>();
        //TODO: implement case for ref and spec table
        for (var interfaceRow : component.getTableHeads().getInterfaceImplTableHead()) {
            if (interfaceExtendsClass(interfaceRow, className, classNamespace, component)) {
                interfaces.add(getInterface(interfaceRow, component));
            }
        }

        //TODO: filter out nulls -> can be removed when implemented case for ref and spec table
        return interfaces.stream().filter(x -> x != null).toList().toArray(new IType[0]);
    }

    private static boolean interfaceExtendsClass(CLIInterfaceImplTableRow interfaceRow, String extendingClassName, String extendingClassNamespace, IComponent component) {
        var potentialExtendingClassRow = component.getTableHeads().getTypeDefTableHead().skip(interfaceRow.getKlassTablePtr());
        //we can not create the whole klass because of circular dependency, we only need the name and namespace
        var potentialClassName = component.getTypeName(potentialExtendingClassRow);
        var potentialClassNamespace = component.getTypeNamespace(potentialExtendingClassRow);

        return extendingClassName.equals(potentialClassName) && extendingClassNamespace.equals(potentialClassNamespace);
    }

    private static IType getInterface(CLIInterfaceImplTableRow row, IComponent component) {
        CLITablePtr tablePtr = row.getInterfaceTablePtr();
        return switch (tablePtr.getTableId()) {
            case CLITableConstants.CLI_TABLE_TYPE_DEF -> component.getLocalType(tablePtr);
            case CLITableConstants.CLI_TABLE_TYPE_REF -> null; //TODO: implement case for ref table
            case CLITableConstants.CLI_TABLE_TYPE_SPEC -> null; //TODO: implement case for spec table
            default -> throw new NotImplementedException();
        };
    }

    private static IType getDirectBaseClass(CLITypeDefTableRow typeDefRow, IComponent component) {
        var extendsTable = typeDefRow.getExtendsTablePtr();
        if (extendsTable.isEmpty()) return null;

        IType baseClass;
        switch (extendsTable.getTableId()) {
            case CLITableConstants.CLI_TABLE_TYPE_DEF -> baseClass = component.getLocalType(extendsTable);
            case CLITableConstants.CLI_TABLE_TYPE_REF -> baseClass = null; //TODO: implement case for ref table
            case CLITableConstants.CLI_TABLE_TYPE_SPEC -> baseClass = null; //TODO: implement case for spec table
            default -> throw new NotImplementedException();
        }
        return baseClass;
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
    //endregion
}
