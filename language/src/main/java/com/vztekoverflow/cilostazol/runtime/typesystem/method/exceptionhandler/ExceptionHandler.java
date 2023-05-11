package com.vztekoverflow.cilostazol.runtime.typesystem.method.exceptionhandler;

import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public class ExceptionHandler implements IExceptionHandler {
    private final int _tryOffset;
    private final int _tryLength;
    private final int _handlerOffset;
    private final int _handlerLength;
    private final IType _handlerException;
    private final ExceptionHandlerType _type;

    public ExceptionHandler(int _tryOffset, int _tryLength, int _handlerOffset, int _handlerLength, IType _handlerException, ExceptionHandlerType type) {
        this._tryOffset = _tryOffset;
        this._tryLength = _tryLength;
        this._handlerOffset = _handlerOffset;
        this._handlerLength = _handlerLength;
        this._handlerException = _handlerException;
        _type = type;
    }

    @Override
    public int getTryOffset() {
        return 0;
    }

    @Override
    public int getTryLength() {
        return 0;
    }

    @Override
    public int getHandlerOffset() {
        return 0;
    }

    @Override
    public int getHandlerLength() {
        return 0;
    }

    @Override
    public IType getHandlingException() {
        return null;
    }

    @Override
    public ExceptionHandlerType getType() {
        return null;
    }
}
