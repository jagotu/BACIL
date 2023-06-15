package com.vztekoverflow.cilostazol.runtime.typesystem.method.exceptionhandler;

import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitutable;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public interface IExceptionHandler extends ISubstitutable<IExceptionHandler, IType> {
  public int getTryOffset();

  public int getTryLength();

  public int getHandlerOffset();

  public int getHandlerLength();

  public IType getHandlingException();

  public ExceptionClauseFlags getFlags();
}
