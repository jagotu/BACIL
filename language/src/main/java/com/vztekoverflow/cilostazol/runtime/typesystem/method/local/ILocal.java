package com.vztekoverflow.cilostazol.runtime.typesystem.method.local;

import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitutable;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public interface ILocal extends ISubstitutable<ILocal, IType> {
  boolean isPinned();

  boolean isByRef();

  IType getType();
}
