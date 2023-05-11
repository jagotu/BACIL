package com.vztekoverflow.cilostazol.runtime.typesystem.method.exceptionhandler;

import com.vztekoverflow.cilostazol.runtime.typesystem.method.exceptionhandler.ExceptionHandlerType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public interface IExceptionHandler {
    public int getTryOffset();
    public int getTryLength();
    public int getHandlerOffset();
    public int getHandlerLength();
    public IType getHandlingException();
    public ExceptionHandlerType getType();
}
