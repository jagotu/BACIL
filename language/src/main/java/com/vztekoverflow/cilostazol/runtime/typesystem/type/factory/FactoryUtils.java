package com.vztekoverflow.cilostazol.runtime.typesystem.type.factory;

import com.vztekoverflow.cil.parser.cli.signature.TypeSig;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.generated.CLIGenericParamConstraintTableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.CLIGenericParamTableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITableConstants;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeDefTableRow;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.runtime.typesystem.TypeSystemException;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.CLIComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ITypeParameter;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.TypeParameter;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.VarianceType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

import java.util.ArrayList;
import java.util.List;

public class FactoryUtils {
    //region Constants
    private static final short GP_VARIANCE_MASK = 0x0003;
    private static final short GP_NONE = 0x0000;
    private static final short GP_COVARIANT = 0x0001;
    private static final short GP_CONTRAVARIANT = 0x0002;
    private static final short GP_SPECIAL_CONSTRAINT_MASK = 0x001C;
    private static final short GP_REFERENCE_TYPE_CONSTRAINT = 0x0004;
    private static final short GP_NOT_NULLABLE_VALUE_TYPE_CONSTRAINT = 0x0008;
    private static final short GP_DEFAULT_CONSTRUCTOR_CONSTRAINT = 0x0010;
    //endregion

    //region GenericParameters
    private static boolean hasNone(short flag)
    {
        return (flag & GP_VARIANCE_MASK) != GP_NONE;
    }

    public static boolean hasNewConstraint(short flag)
    {
        return !hasNone(flag) && (flag & GP_SPECIAL_CONSTRAINT_MASK) == GP_DEFAULT_CONSTRUCTOR_CONSTRAINT;
    }
    public static boolean hasValueTypeConstraint(short flag)
    {
        return !hasNone(flag) && (flag & GP_SPECIAL_CONSTRAINT_MASK) == GP_NOT_NULLABLE_VALUE_TYPE_CONSTRAINT;
    }
    public static boolean hasClassConstraint(short flag)
    {
        return !hasNone(flag) && (flag & GP_SPECIAL_CONSTRAINT_MASK) == GP_REFERENCE_TYPE_CONSTRAINT;
    }
    public static VarianceType getVariance(short flag)
    {
        return switch (flag & GP_VARIANCE_MASK)
                {
                    case GP_NONE -> VarianceType.Invariant;
                    case GP_COVARIANT -> VarianceType.Covariant;
                    case GP_CONTRAVARIANT -> VarianceType.Contravariant;
                    default -> throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.parser.variance"));
                };
    }

    public static ITypeParameter createTypeParameter(CLIGenericParamTableRow row, IType[] mvars, IType[] vars, IComponent definingComponent, short flags)
    {
        return new TypeParameter(getConstrains(row, mvars, vars, definingComponent), definingComponent, hasNewConstraint(flags), hasClassConstraint(flags), hasValueTypeConstraint(flags), getVariance(flags));
    }

    public static ITypeParameter[] getTypeParameters(int count, CLITablePtr ptr, IType[] vars, CLIComponent component)
    {
        ITypeParameter[] result = new TypeParameter[count];
        while (count-- > 0)
        {
            for(CLIGenericParamTableRow row : component.getDefiningFile().getTableHeads().getGenericParamTableHead())
            {
                if (ptr.getTableId() == row.getOwnerTablePtr().getTableId() && ptr.getRowNo() == row.getOwnerTablePtr().getRowNo())
                {
                    result[row.getNumber()] = FactoryUtils.createTypeParameter(row, result, vars, component, row.getFlags());
                }
            }
        }

        return result;
    }

    public static ITypeParameter[] getTypeParameters(CLITypeDefTableRow ptr, CLIComponent component)
    {
        var typeParameters = new ArrayList<CLIGenericParamTableRow>();
        for (CLIGenericParamTableRow row : component.getTableHeads().getGenericParamTableHead()) {
            if (ptr.getTableId() == row.getTableId() && ptr.getRowNo() == row.getRowNo()) {
                typeParameters.add(row);
            }
        }

        ITypeParameter[] result = new TypeParameter[typeParameters.size()];
        for (CLIGenericParamTableRow row : typeParameters) {
            result[row.getNumber()] = FactoryUtils.createTypeParameter(row, null, result, component, row.getFlags());
        }

        return result;
    }
    //endregion

    //region GenericParametersConstraints
    public static IType[] getConstrains(CLIGenericParamTableRow row, IType[] mvars, IType[] vars, IComponent component)
    {
        List<IType> constrains = new ArrayList<>();
        for(CLIGenericParamConstraintTableRow r : component.getDefiningFile().getTableHeads().getGenericParamConstraintTableHead())
        {
            if (row.getTableId() == r.getOwnerTablePtr().getTableId() && row.getRowNo() == r.getOwnerTablePtr().getRowNo())
            {
                constrains.add(createType(r.getConstraintTablePtr(), mvars, vars, component));
            }
        }

        return constrains.toArray(new IType[0]);
    }

    public static IType createType(CLITablePtr ptr, IType[] mvars, IType[] vars, IComponent component)
    {
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
    //endregion

    public static IType create(TypeSig tSig, IType[] mvars, IType[] vars, IComponent definingComponent)
    {
        //TODO: implement and move out of factory utils, it does not make sense seeing FactoryUtils.create(...)
        return null;
    }
}
