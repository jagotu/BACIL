package com.vztekoverflow.cilostazol.runtime.typesystem.type.factory;

import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.generated.CLIGenericParamTableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.CLIInterfaceImplTableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITableConstants;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeDefTableRow;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;
import com.vztekoverflow.cilostazol.runtime.typesystem.TypeSystemException;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.CLIComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ITypeParameter;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.TypeParameter;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;
import com.vztekoverflow.cilostazol.runtime.typesystem.typeparameters.factory.TypeParameterFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class FactoryUtils {
    static ITypeParameter[] getTypeParameters(CILOSTAZOLContext context, CLITypeDefTableRow ptr, CLIComponent component) {
        var typeParameters = new ArrayList<CLIGenericParamTableRow>();
        for (CLIGenericParamTableRow row : component.getTableHeads().getGenericParamTableHead()) {
            final var genParamOwner = row.getOwnerTablePtr();
            if (CLITableConstants.CLI_TABLE_TYPE_DEF == genParamOwner.getTableId() && ptr.getRowNo() == genParamOwner.getRowNo()) {
                typeParameters.add(row);
            }
        }

        ITypeParameter[] result = new TypeParameter[typeParameters.size()];
        for (CLIGenericParamTableRow row : typeParameters) {
            result[row.getNumber()] = TypeParameterFactory.create(context, row, null, result, component, component.getDefiningFile());
        }

        return result;
    }

    static IType[] getInterfaces(CILOSTAZOLContext context, String className, String classNamespace, CLIComponent component) {
        List<IType> interfaces = new ArrayList<>();
        //TODO: implement case for ref and spec table
        for (var interfaceRow : component.getTableHeads().getInterfaceImplTableHead()) {
            if (interfaceExtendsClass(interfaceRow, className, classNamespace, component)) {
                interfaces.add(getInterface(context, interfaceRow, component));
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

    static IType getInterface(CILOSTAZOLContext context, CLIInterfaceImplTableRow row, CLIComponent component) {
        CLITablePtr tablePtr = row.getInterfaceTablePtr();
        return switch (tablePtr.getTableId()) {
            case CLITableConstants.CLI_TABLE_TYPE_DEF -> component.getLocalType(context, tablePtr);
            case CLITableConstants.CLI_TABLE_TYPE_REF -> null; //TODO: implement case for ref table
            case CLITableConstants.CLI_TABLE_TYPE_SPEC -> null; //TODO: implement case for spec table
            default -> throw new NotImplementedException();
        };
    }

    static IType getDirectBaseClass(CILOSTAZOLContext context, CLITypeDefTableRow typeDefRow, IType[] mvars, IType[] vars,CLIComponent component) {
        CLITablePtr tablePtr = typeDefRow.getExtendsTablePtr();
        if (tablePtr.isEmpty()) return null;

        IType baseClass;
        switch (tablePtr.getTableId()) {
            case CLITableConstants.CLI_TABLE_TYPE_DEF -> baseClass = component.getLocalType(context, tablePtr);
            case CLITableConstants.CLI_TABLE_TYPE_REF -> baseClass = TypeFactory.create(context, component.getDefiningFile().getTableHeads().getTypeRefTableHead().skip(tablePtr), component);
            case CLITableConstants.CLI_TABLE_TYPE_SPEC -> baseClass = TypeFactory.create(context, component.getDefiningFile().getTableHeads().getTypeSpecTableHead().skip(tablePtr), mvars, vars, component);
            default -> throw new NotImplementedException();
        }
        return baseClass;
    }

    static IType getTypeFromDifferentModule(CILOSTAZOLContext context, CLIComponent component, String name, String namespace, CLITablePtr resolutionScopeTablePtr) {
        //TODO: I feel like getting tables should be done via something else that component -> some assembly appDomain/Assembly singleton maybe?
        var moduleRefName = component.getNameFrom(component.getTableHeads().getModuleRefTableHead().skip(resolutionScopeTablePtr));

        //We can omit looking for file since we know that it is in the same assembly as this
        var definingComponent = Arrays.stream(component.getDefiningAssembly().getComponents())
                .filter(c -> c.getDefiningFile().getName().equals(moduleRefName))
                .findFirst()
                .orElseThrow(() -> new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.moduleRefNotFound", moduleRefName)));
        return definingComponent.getLocalType(context, name, namespace);
    }

    static IType getTypeFromDifferentAssembly(CLIComponent component, String name, String namespace, CLITablePtr resolutionScopeTablePtr) {
        var referencedAssemblyIdentity = AssemblyIdentity.fromAssemblyRefRow(component.getDefiningFile().getStringHeap(), component.getTableHeads().getAssemblyRefTableHead().skip(resolutionScopeTablePtr));
        var referencedAssembly = component.getDefiningAssembly().getAppDomain().getAssembly(referencedAssemblyIdentity);
        if (referencedAssembly == null) {
            //TODO: log CILOSTAZOLBundle.message("cilostazol.warning.referencedAssemblyNotFound", referencedAssemblyIdentity.toString())
            return null;
        }
        return referencedAssembly.getLocalType(namespace, name);
    }
}
