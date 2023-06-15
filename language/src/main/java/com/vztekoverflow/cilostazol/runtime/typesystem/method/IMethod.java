package com.vztekoverflow.cilostazol.runtime.typesystem.method;

import com.oracle.truffle.api.nodes.RootNode;
import com.vztekoverflow.cil.parser.cli.signature.MethodDefFlags;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitutable;
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

public interface IMethod extends ISubstitutable<IMethod, IType> {
  // Containing definition
  public IComponent getDefiningComponent();

  public IType getDefiningType();

  public String getName();

  // Signature
  public ITypeParameter[] getTypeParameters();

  public IParameter[] getParameters();

  public ILocal[] getLocals();

  public IReturnType getReturnType();

  // Flags
  public MethodDefFlags getMethodDefFlags();

  public MethodFlags getMethodFlags();

  public MethodImplFlags getMethodImplFlags();

  public MethodHeaderFlags getMethodHeaderFlags();

  // Body
  public IExceptionHandler[] getExceptionHandlers();

  public int getMaxStack();

  // Truffle execution
  public RootNode getNode();

  // Substitution
  public IMethod substitute(ISubstitution<IType> substitution);

  public IMethod getDefinition();

  public IMethod getConstructedFrom();
}
