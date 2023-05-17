package com.vztekoverflow.cilostazol.runtime.typesystem.method.exceptionhandler;

import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitution;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public class ExceptionHandler implements IExceptionHandler {
    private final int _tryOffset;
    private final int _tryLength;
    private final int _handlerOffset;
    private final int _handlerLength;
    private final IType _handlerException;
    private final ExceptionClauseFlags _flags;

    public ExceptionHandler(int _tryOffset, int _tryLength, int _handlerOffset, int _handlerLength, IType _handlerException, ExceptionClauseFlags flags) {
        this._tryOffset = _tryOffset;
        this._tryLength = _tryLength;
        this._handlerOffset = _handlerOffset;
        this._handlerLength = _handlerLength;
        this._handlerException = _handlerException;
        _flags = flags;
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
    public ExceptionClauseFlags getFlags() {
        return _flags;
    }

    @Override
    public IExceptionHandler substitute(ISubstitution<IType> substitution) {
        throw new NotImplementedException();
    }

    @Override
    public IExceptionHandler getDefinition() {
        throw new NotImplementedException();
    }

    @Override
    public IExceptionHandler getConstructedFrom() {
        throw new NotImplementedException();
    }
}
