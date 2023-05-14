package com.vztekoverflow.cilostazol.runtime.typesystem.method.factory;

import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.generated.CLIGenericParamTableRow;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.CLIComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ITypeParameter;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.TypeParameter;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;
import com.vztekoverflow.cilostazol.runtime.typesystem.typeparameters.factory.TypeParameterFactory;

public final class FactoryUtils {
    static ITypeParameter[] getTypeParameters(int count, CLITablePtr ptr, IType[] vars, CLIComponent component) {
        ITypeParameter[] result = new TypeParameter[count];
        while (count-- > 0) {
            for (CLIGenericParamTableRow row : component.getDefiningFile().getTableHeads().getGenericParamTableHead()) {
                if (ptr.getTableId() == row.getOwnerTablePtr().getTableId() && ptr.getRowNo() == row.getOwnerTablePtr().getRowNo()) {
                    result[row.getNumber()] = TypeParameterFactory.create(row, result, vars, component);
                }
            }
        }

        return result;
    }
}
