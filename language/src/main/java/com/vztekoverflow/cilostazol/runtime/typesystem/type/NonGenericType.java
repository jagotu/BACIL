package com.vztekoverflow.cilostazol.runtime.typesystem.type;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cil.parser.cli.table.CLITableRow;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.typesystem.TypeSystemException;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.CLIComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitution;

public class NonGenericType<T extends CLITableRow<T>> extends TypeBase<T> {

  public NonGenericType(
      CILOSTAZOLContext context,
      T row,
      CLIFile _definingFile,
      String _name,
      String _namespace,
      IType _directBaseClass,
      IType[] _interfaces,
      CLIComponent _definingComponent,
      int flags) {
    super(
        context,
        row,
        _definingFile,
        _name,
        _namespace,
        _directBaseClass,
        _interfaces,
        _definingComponent,
        flags);
  }

  // region TypeBase
  @Override
  public IType[] getTypeParameters() {
    throw new TypeSystemException(
        CILOSTAZOLBundle.message("cilostazol.exception.invalidOperation"));
  }

  @Override
  public IType substitute(ISubstitution<IType> substitution) {
    throw new TypeSystemException(
        CILOSTAZOLBundle.message("cilostazol.exception.invalidOperation"));
  }

  @Override
  public IType getConstructedFrom() {
    throw new TypeSystemException(
        CILOSTAZOLBundle.message("cilostazol.exception.invalidOperation"));
  }

  // endregion
}
