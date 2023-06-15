package com.vztekoverflow.cilostazol.runtime.typesystem.method.parameter;

import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitutable;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public interface IParameter extends ISubstitutable<IParameter, IType> {
  boolean isByRef();

  ParamFlags getFlags();

  int getIndex();

  public String getName();

  IType getType();
}
