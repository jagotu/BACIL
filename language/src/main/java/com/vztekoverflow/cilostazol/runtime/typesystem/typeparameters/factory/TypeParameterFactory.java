package com.vztekoverflow.cilostazol.runtime.typesystem.typeparameters.factory;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cil.parser.cli.table.generated.CLIGenericParamConstraintTableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.CLIGenericParamTableRow;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.CLIComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.GenericParameterFlags;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.TypeParameter;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.factory.TypeFactory;
import java.util.ArrayList;
import java.util.List;

public final class TypeParameterFactory {
  public static TypeParameter create(
      CLIGenericParamTableRow row,
      IType[] mvars,
      IType[] vars,
      CLIComponent definingComponent,
      CLIFile file) {
    final short flags = row.getFlags();
    return new TypeParameter(
        getConstrains(row, mvars, vars, definingComponent),
        new GenericParameterFlags(flags),
        row.getNumber(),
        row.getNameHeapPtr().read(file.getStringHeap()));
  }

  private static IType[] getConstrains(
      CLIGenericParamTableRow row, IType[] mvars, IType[] vars, CLIComponent component) {
    List<IType> constrains = new ArrayList<>();
    for (CLIGenericParamConstraintTableRow r :
        component.getDefiningFile().getTableHeads().getGenericParamConstraintTableHead()) {
      if (row.getTableId() == r.getOwnerTablePtr().getTableId()
          && row.getRowNo() == r.getOwnerTablePtr().getRowNo()) {
        constrains.add(TypeFactory.create(r.getConstraintTablePtr(), mvars, vars, component));
      }
    }

    return constrains.toArray(new IType[0]);
  }
}
