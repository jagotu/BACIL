package com.vztekoverflow.cilostazol.runtime.typesystem.method;

import com.oracle.truffle.api.nodes.RootNode;
import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cil.parser.cli.signature.MethodDefFlags;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.nodes.CILOSTAZOLRootNode;
import com.vztekoverflow.cilostazol.runtime.typesystem.TypeSystemException;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.CLIComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitution;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ITypeParameter;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.exceptionhandler.IExceptionHandler;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.flags.MethodFlags;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.flags.MethodHeaderFlags;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.flags.MethodImplFlags;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.local.ILocal;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.parameter.IParameter;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.returnType.IReturnType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public class NonGenericMethod extends MethodBase {

  public NonGenericMethod(
      CLIFile definingFile,
      String name,
      CLIComponent definingComponent,
      IType definingType,
      MethodDefFlags methodDefFlags,
      MethodFlags methodFlags,
      MethodImplFlags methodImplFlags,
      MethodHeaderFlags methodHeaderFlags,
      IParameter[] parameters,
      ILocal[] locals,
      IReturnType retType,
      IExceptionHandler[] exceptionHandlers,
      byte[] cil,
      int maxStack) {
    super(
        definingFile,
        name,
        definingComponent,
        definingType,
        methodDefFlags,
        methodFlags,
        methodImplFlags,
        methodHeaderFlags,
        parameters,
        locals,
        retType,
        exceptionHandlers,
        cil,
        maxStack);
  }

  // region MethodBase
  @Override
  public ITypeParameter[] getTypeParameters() {
    throw new TypeSystemException(
        CILOSTAZOLBundle.message("cilostazol.exception.invalidOperation"));
  }

  @Override
  public IMethod substitute(ISubstitution<IType> substitution) {
    return new SubstitutedGenericMethod(this, getDefinition(), substitution);
  }

  @Override
  public IMethod getConstructedFrom() {
    throw new TypeSystemException(
        CILOSTAZOLBundle.message("cilostazol.exception.invalidOperation"));
  }

  @Override
  public RootNode getNode() {
    // if (_node == null) _node = CILOSTAZOLRootNode.create(this, _cil);

    return null;
  }
  // endregion
}
