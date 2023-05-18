package com.vztekoverflow.cilostazol.runtime.typesystem.type.factory;

import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.generated.CLIGenericParamTableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.CLIInterfaceImplTableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITableConstants;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeDefTableRow;
import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.CLIComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ITypeParameter;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.TypeParameter;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;
import com.vztekoverflow.cilostazol.runtime.typesystem.typeparameters.factory.TypeParameterFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class FactoryUtils {
    static ITypeParameter[] getTypeParameters(CLITypeDefTableRow ptr, CLIComponent component) {
        var typeParameters = new ArrayList<CLIGenericParamTableRow>();
        for (CLIGenericParamTableRow row : component.getTableHeads().getGenericParamTableHead()) {
            final var genParamOwner = row.getOwnerTablePtr();
            if (CLITableConstants.CLI_TABLE_TYPE_DEF == genParamOwner.getTableId() && ptr.getRowNo() == genParamOwner.getRowNo()) {
                typeParameters.add(row);
            }
        }

        ITypeParameter[] result = new TypeParameter[typeParameters.size()];
        for (CLIGenericParamTableRow row : typeParameters) {
            result[row.getNumber()] = TypeParameterFactory.create(row, null, result, component, component.getDefiningFile());
        }

        return result;
    }

    static IType[] getInterfaces(String className, String classNamespace, CLIComponent component) {
        List<IType> interfaces = new ArrayList<>();
        //TODO: implement case for ref and spec table
        for (var interfaceRow : component.getTableHeads().getInterfaceImplTableHead()) {
            if (interfaceExtendsClass(interfaceRow, className, classNamespace, component)) {
                interfaces.add(getInterface(interfaceRow, component));
            }
        }

        //TODO: filter out nulls -> can be removed when implemented case for ref and spec table
        return interfaces.stream().filter(Objects::nonNull).toList().toArray(new IType[0]);
    }

    static boolean interfaceExtendsClass(CLIInterfaceImplTableRow interfaceRow, String extendingClassName, String extendingClassNamespace, CLIComponent component) {
        var potentialExtendingClassRow = component.getTableHeads().getTypeDefTableHead().skip(interfaceRow.getKlassTablePtr());
        //we can not create the whole klass because of circular dependency, we only need the name and namespace
        var potentialClassName = component.getNameFrom(potentialExtendingClassRow);
        var potentialClassNamespace = component.getNamespaceFrom(potentialExtendingClassRow);

        return extendingClassName.equals(potentialClassName) && extendingClassNamespace.equals(potentialClassNamespace);
    }

    static IType getInterface(CLIInterfaceImplTableRow row, CLIComponent component) {
        CLITablePtr tablePtr = row.getInterfaceTablePtr();
        return switch (tablePtr.getTableId()) {
            case CLITableConstants.CLI_TABLE_TYPE_DEF -> component.getLocalType(tablePtr);
            case CLITableConstants.CLI_TABLE_TYPE_REF -> null; //TODO: implement case for ref table
            case CLITableConstants.CLI_TABLE_TYPE_SPEC -> null; //TODO: implement case for spec table
            default -> throw new NotImplementedException();
        };
    }

    static IType getDirectBaseClass(CLITypeDefTableRow typeDefRow, CLIComponent component) {
        CLITablePtr tablePtr = typeDefRow.getExtendsTablePtr();
        if (tablePtr.isEmpty()) return null;

        IType baseClass;
        switch (tablePtr.getTableId()) {
            case CLITableConstants.CLI_TABLE_TYPE_DEF -> baseClass = component.getLocalType(tablePtr);
            case CLITableConstants.CLI_TABLE_TYPE_REF -> baseClass = null; //TODO: implement case for ref table
            case CLITableConstants.CLI_TABLE_TYPE_SPEC -> baseClass = null; //TODO: implement case for spec table
            default -> throw new NotImplementedException();
        }
        return baseClass;
    }
}
