package com.vztekoverflow.cilostazol.runtime.typesystem.type.factory;

import com.vztekoverflow.cil.parser.cli.signature.ElementTypeFlag;
import com.vztekoverflow.cil.parser.cli.signature.SignatureReader;
import com.vztekoverflow.cil.parser.cli.signature.TypeSig;
import com.vztekoverflow.cil.parser.cli.signature.TypeSpecSig;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITableConstants;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeDefTableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeRefTableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeSpecTableRow;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.exceptions.InvalidCLIException;
import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.typesystem.TypeSystemException;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.CLIComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.Substitution;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.NonGenericType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.OpenGenericType;

public final class TypeFactory {
    public static IType create(CLITablePtr ptr, IType[] mvars, IType[] vars, CLIComponent component) {
        return switch (ptr.getTableId()) {
            case CLITableConstants.CLI_TABLE_TYPE_DEF ->
                    component.getLocalType(ptr);
            case CLITableConstants.CLI_TABLE_TYPE_REF ->
                    TypeFactory.create(component.getDefiningFile().getTableHeads().getTypeRefTableHead().skip(ptr), component);
            case CLITableConstants.CLI_TABLE_TYPE_SPEC ->
                    TypeFactory.create(component.getDefiningFile().getTableHeads().getTypeSpecTableHead().skip(ptr), mvars, vars, component);
            default ->
                    throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.constructor.withoutDefType"));
        };
    }

    public static IType create(CLITypeDefTableRow typeDefRow, CLIComponent component) {
        CILOSTAZOLContext context = component.getContext();
        String name = component.getNameFrom(typeDefRow);
        String namespace = component.getNamespaceFrom(typeDefRow);
        IType[] interfaces = FactoryUtils.getInterfaces(name, namespace, component);
        IType[] genericParameters = FactoryUtils.getTypeParameters(typeDefRow, component);
        IType directBaseClass = FactoryUtils.getDirectBaseClass(typeDefRow, new IType[0], genericParameters,component);
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

    public static IType create(CLITypeSpecTableRow type, IType[] methodTypeParameters, IType[] classTypeParameters, CLIComponent component) {
        TypeSig signature = TypeSig.read(new SignatureReader(type.getSignatureHeapPtr().read(component.getDefiningFile().getBlobHeap())), component.getDefiningFile());
        return create(signature, methodTypeParameters, classTypeParameters, component);
    }

    public static IType create(CLITypeRefTableRow type, CLIComponent component) {
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
                    FactoryUtils.getTypeFromDifferentModule(component, name, namespace, resolutionScopeTablePtr);
            case CLITableConstants.CLI_TABLE_MODULE -> throw new InvalidCLIException();
            case CLITableConstants.CLI_TABLE_ASSEMBLY_REF ->
                    FactoryUtils.getTypeFromDifferentAssembly(component, name, namespace, resolutionScopeTablePtr);
            default ->
                    throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.unknownResolutionScope", namespace, name, resolutionScopeTablePtr.getTableId()));
        };
    }

    public static IType create(TypeSpecSig typeSig, IType[] mvars, IType[] vars, CLIComponent definingComponent) {
        //TODO: null reference exception might have occured here if TypeSig is not created from CLASS
        //TODO: resolve for other types (SZARRAY, GENERICINST, ...)
        if (typeSig.getFlag().getFlag() == ElementTypeFlag.Flag.ELEMENT_TYPE_GENERICINST)
        {
            IType genType = TypeFactory.create(typeSig.getGenType(), mvars, vars, definingComponent);
            IType[] typeArgs = new IType[typeSig.getTypeArgs().length];
            for (int i = 0; i < typeArgs.length; i++) {
                typeArgs[i] = TypeFactory.create(typeSig.getTypeArgs()[i], mvars, vars, definingComponent);
            }
            return genType.substitute(new Substitution<>(genType.getTypeParameters(), typeArgs));
        }
        else if (typeSig.getFlag().getFlag() == ElementTypeFlag.Flag.ELEMENT_TYPE_MVAR)
        {
            return null;
        }
        else if (typeSig.getFlag().getFlag() == ElementTypeFlag.Flag.ELEMENT_TYPE_VAR)
        {
            return null;
        }
        else
        {
            return null;
        }
    }

    public static IType create(TypeSig typeSig, IType[] mvars, IType[] vars, CLIComponent definingComponent) {
        //TODO: null reference exception might have occured here if TypeSig is not created from CLASS
        //TODO: resolve for other types (SZARRAY, GENERICINST, ...)
        if (typeSig.getElementType() == TypeSig.ELEMENT_TYPE_VALUETYPE
            || typeSig.getElementType() == TypeSig.ELEMENT_TYPE_CLASS)
            return create(typeSig.getCliTablePtr(), mvars, vars, definingComponent);
        else if (typeSig.getElementType() == TypeSig.ELEMENT_TYPE_VAR)
        {
            return vars[typeSig.getIndex()];
        }
        else if (typeSig.getElementType() == TypeSig.ELEMENT_TYPE_MVAR)
        {
            return mvars[typeSig.getIndex()];
        }
        else if (typeSig.getElementType() == TypeSig.ELEMENT_TYPE_GENERICINST)
        {
            IType genType = TypeFactory.create(typeSig.getCliTablePtr(), mvars, vars, definingComponent);
            IType[] typeArgs = new IType[typeSig.getTypeArgs().length];
            for (int i = 0; i < typeArgs.length; i++) {
                typeArgs[i] = TypeFactory.create(typeSig.getTypeArgs()[i], mvars, vars, definingComponent);
            }
            return genType.substitute(new Substitution<>(genType.getTypeParameters(), typeArgs));
        }
        else
        {
            return null;
        }
    }
}
