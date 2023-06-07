package com.vztekoverflow.cilostazol.runtime.typesystem.type.factory;

import com.vztekoverflow.cil.parser.cli.signature.TypeSig;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITableConstants;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeDefTableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeRefTableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeSpecTableRow;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.CILOSTAZOLLanguage;
import com.vztekoverflow.cilostazol.context.ContextAccess;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.exceptions.InvalidCLIException;
import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;
import com.vztekoverflow.cilostazol.runtime.typesystem.TypeSystemException;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.CLIComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.NonGenericType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.OpenGenericType;

public final class TypeFactory {
    public static IType create(CILOSTAZOLContext context, CLITablePtr ptr, IType[] mvars, IType[] vars, CLIComponent component) {
        return switch (ptr.getTableId()) {
            case CLITableConstants.CLI_TABLE_TYPE_DEF ->
                    TypeFactory.create(context, component.getDefiningFile().getTableHeads().getTypeDefTableHead().skip(ptr), component);
            case CLITableConstants.CLI_TABLE_TYPE_REF ->
                    TypeFactory.create(context, component.getDefiningFile().getTableHeads().getTypeRefTableHead().skip(ptr), component);
            case CLITableConstants.CLI_TABLE_TYPE_SPEC ->
                    TypeFactory.create(context, component.getDefiningFile().getTableHeads().getTypeSpecTableHead().skip(ptr), mvars, vars, component);
            default ->
                    throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.constructor.withoutDefType"));
        };
    }

    public static IType create(CILOSTAZOLContext context, CLITypeDefTableRow typeDefRow, CLIComponent component) {
        String name = component.getNameFrom(typeDefRow);
        String namespace = component.getNamespaceFrom(typeDefRow);
        IType directBaseClass = FactoryUtils.getDirectBaseClass(context, typeDefRow, component);
        IType[] interfaces = FactoryUtils.getInterfaces(context, name, namespace, component);
        IType[] genericParameters = FactoryUtils.getTypeParameters(context, typeDefRow, component);
        int flags = typeDefRow.getFlags();
        if (genericParameters.length == 0) {
            return new NonGenericType<>(context,
                    typeDefRow,
                    component.getDefiningFile(),
                    name,
                    namespace,
                    directBaseClass,
                    interfaces,
                    component,
                    flags);
        } else {
            return new OpenGenericType<>(context,
                    typeDefRow,
                    component.getDefiningFile(),
                    name,
                    namespace,
                    directBaseClass,
                    interfaces,
                    component,
                    genericParameters,
                    flags);
        }
    }

    public static IType create(CILOSTAZOLContext context, CLITypeSpecTableRow type, IType[] methodTypeParameters, IType[] classTypeParameters, CLIComponent component) {
        return null;
    }

    public static IType create(CILOSTAZOLContext context,CLITypeRefTableRow type, CLIComponent component) {
        var name = component.getNameFrom(type);
        var namespace = component.getNamespaceFrom(type);

        var resolutionScopeTablePtr = type.getResolutionScopeTablePtr();
        if (resolutionScopeTablePtr == null) {
            //TODO: find in ExportedType table
            throw new NotImplementedException();
        }

        return switch (resolutionScopeTablePtr.getTableId()) {
            case CLITableConstants.CLI_TABLE_TYPE_REF ->
                    throw new UnsupportedOperationException(CILOSTAZOLBundle.message("cilostazol.exception.typeRefResolutionScope"));
            case CLITableConstants.CLI_TABLE_MODULE_REF ->
                    FactoryUtils.getTypeFromDifferentModule(context, component, name, namespace, resolutionScopeTablePtr);
            case CLITableConstants.CLI_TABLE_MODULE -> throw new InvalidCLIException();
            case CLITableConstants.CLI_TABLE_ASSEMBLY_REF ->
                    FactoryUtils.getTypeFromDifferentAssembly(component, name, namespace, resolutionScopeTablePtr);
            default ->
                    throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.unknownResolutionScope", namespace, name, resolutionScopeTablePtr.getTableId()));
        };
    }

    public static IType create(CILOSTAZOLContext context, TypeSig typeSig, IType[] mvars, IType[] vars, CLIComponent definingComponent) {
        //TODO: null reference exception might have occured here if TypeSig is not created from CLASS
        //TODO: resolve for other types (SZARRAY, GENERICINST, ...)
        if (typeSig.getCliTablePtr() == null) return null;
        return create(context, typeSig.getCliTablePtr(), mvars, vars, definingComponent);
    }
}
