package com.vztekoverflow.cilostazol.runtime.typesystem.method;

import com.oracle.truffle.api.nodes.RootNode;
import com.vztekoverflow.cil.parser.cli.signature.MethodDefFlags;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
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
import java.util.Arrays;

public class SubstitutedGenericMethod implements IMethod, ICILBasedMethod {
  protected final IMethod _definition;
  protected final ICILBasedMethod _constructedFrom;
  protected final ISubstitution<IType> _substitution;
  protected RootNode _node;

  public SubstitutedGenericMethod(
      ICILBasedMethod _constructedFrom, IMethod _definition, ISubstitution<IType> substitution) {
    this._definition = _definition;
    this._constructedFrom = _constructedFrom;
    this._substitution = substitution;
    _node = null;
  }

  // region IMethod
  @Override
  public String getName() {
    return _definition.getName();
  }

  @Override
  public IParameter[] getParameters() {
    return Arrays.stream(_constructedFrom.getParameters())
        .map(x -> x.substitute(_substitution))
        .toArray(IParameter[]::new);
  }

  @Override
  public ILocal[] getLocals() {
    return Arrays.stream(_constructedFrom.getLocals())
        .map(x -> x.substitute(_substitution))
        .toArray(ILocal[]::new);
  }

  @Override
  public ITypeParameter[] getTypeParameters() {
    return Arrays.stream(_constructedFrom.getTypeParameters())
        .map(x -> x.substitute(_substitution))
        .toArray(ITypeParameter[]::new);
  }

  @Override
  public IReturnType getReturnType() {
    return _constructedFrom.getReturnType().substitute(_substitution);
  }

  @Override
  public MethodDefFlags getMethodDefFlags() {
    return _definition.getMethodDefFlags();
  }

  @Override
  public MethodFlags getMethodFlags() {
    return _definition.getMethodFlags();
  }

  @Override
  public MethodImplFlags getMethodImplFlags() {
    return _definition.getMethodImplFlags();
  }

  @Override
  public MethodHeaderFlags getMethodHeaderFlags() {
    return _definition.getMethodHeaderFlags();
  }

  @Override
  public IExceptionHandler[] getExceptionHandlers() {
    return Arrays.stream(_constructedFrom.getExceptionHandlers())
        .map(x -> x.substitute(_substitution))
        .toArray(IExceptionHandler[]::new);
  }

  @Override
  public IComponent getDefiningComponent() {
    return _constructedFrom.getDefiningComponent();
  }

  @Override
  public IType getDefiningType() {
    return _definition.getDefiningType();
  }

  @Override
  public int getMaxStack() {
    return _definition.getMaxStack();
  }

  @Override
  public IMethod substitute(ISubstitution<IType> substitution) {
    return new SubstitutedGenericMethod(this, _definition, substitution);
  }

  @Override
  public IMethod getDefinition() {
    return _definition;
  }

  @Override
  public IMethod getConstructedFrom() {
    return _constructedFrom;
  }

  @Override
  public RootNode getNode() {
    // if (_node == null) _node = CILOSTAZOLRootNode.create(this, _constructedFrom.getCIL());

    return null;
  }
  // endregion

  // region ICILBasedMethod
  @Override
  public byte[] getCIL() {
    return _constructedFrom.getCIL();
  }
  // endregion
}
