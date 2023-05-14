package com.vztekoverflow.cilostazol.runtime.typesystem.typeparameters.factory;

import com.vztekoverflow.cil.parser.cli.table.generated.CLIGenericParamConstraintTableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.CLIGenericParamTableRow;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.runtime.typesystem.TypeSystemException;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.TypeParameter;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.VarianceType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.factory.TypeFactory;

import java.util.ArrayList;
import java.util.List;

public final class TypeParameterFactory {
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

    public static TypeParameter create(CLIGenericParamTableRow row, IType[] mvars, IType[] vars, IComponent definingComponent) {
        final short flags = row.getFlags();
        return new TypeParameter(
                getConstrains(row, mvars, vars, definingComponent),
                definingComponent,
                hasNewConstraint(flags),
                hasClassConstraint(flags),
                hasValueTypeConstraint(flags),
                getVariance(flags));
    }

    private static IType[] getConstrains(CLIGenericParamTableRow row, IType[] mvars, IType[] vars, IComponent component) {
        List<IType> constrains = new ArrayList<>();
        for (CLIGenericParamConstraintTableRow r : component.getDefiningFile().getTableHeads().getGenericParamConstraintTableHead()) {
            if (row.getTableId() == r.getOwnerTablePtr().getTableId() && row.getRowNo() == r.getOwnerTablePtr().getRowNo()) {
                constrains.add(TypeFactory.create(r.getConstraintTablePtr(), mvars, vars, component));
            }
        }

        return constrains.toArray(new IType[0]);
    }

    private static boolean hasNone(short flag) {
        return (flag & GP_VARIANCE_MASK) != GP_NONE;
    }

    private static boolean hasNewConstraint(short flag) {
        return !hasNone(flag) && (flag & GP_SPECIAL_CONSTRAINT_MASK) == GP_DEFAULT_CONSTRUCTOR_CONSTRAINT;
    }

    private static boolean hasValueTypeConstraint(short flag) {
        return !hasNone(flag) && (flag & GP_SPECIAL_CONSTRAINT_MASK) == GP_NOT_NULLABLE_VALUE_TYPE_CONSTRAINT;
    }

    private static boolean hasClassConstraint(short flag) {
        return !hasNone(flag) && (flag & GP_SPECIAL_CONSTRAINT_MASK) == GP_REFERENCE_TYPE_CONSTRAINT;
    }

    private static VarianceType getVariance(short flag) {
        return switch (flag & GP_VARIANCE_MASK) {
            case GP_NONE -> VarianceType.Invariant;
            case GP_COVARIANT -> VarianceType.Covariant;
            case GP_CONTRAVARIANT -> VarianceType.Contravariant;
            default -> throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.parser.variance"));
        };
    }
}
