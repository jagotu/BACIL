package com.vztekoverflow.cilostazol.runtime.typesystem.method;

import com.oracle.truffle.api.nodes.RootNode;
import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

import java.util.Arrays;

public abstract class MethodBase implements IMethod{
    protected final CLIFile _definingFile;
    protected final String _name;
    protected final boolean _hasThis;
    protected final boolean _hasExplicitType;
    protected final boolean _hasVarArg;
    protected final boolean _isVirtual;
    protected final IParameter[] _parameters;
    protected final IParameter[] _locals;
    protected final IParameter _retType;
    protected final IParameter _this;
    protected final IExceptionHandler[] _exceptionHandlers;
    protected final IComponent _definingComponent;
    protected final IType _definingType;
    protected final int _maxStack;
    protected RootNode _node;

    public MethodBase(CLIFile _definingFile,
                            String _name,
                            boolean _hasThis,
                            boolean _hasExplicitType,
                            boolean _hasVarArg,
                            boolean _isVirtual,
                            IParameter[] _parameters,
                            IParameter[] _locals,
                            IParameter _retType,
                            IParameter _this,
                            IExceptionHandler[] _exceptionHandlers,
                            IComponent _definingComponent,
                            IType _definingType,
                            int maxStack) {
        this._definingFile = _definingFile;
        this._name = _name;
        this._hasThis = _hasThis;
        this._hasExplicitType = _hasExplicitType;
        this._hasVarArg = _hasVarArg;
        this._isVirtual = _isVirtual;
        this._parameters = _parameters;
        this._locals = _locals;
        this._retType = _retType;
        this._this = _this;
        this._exceptionHandlers = _exceptionHandlers;
        this._definingComponent = _definingComponent;
        this._definingType = _definingType;
        this._node = null;
        this._maxStack = maxStack;
    }

    //region IMethod
    @Override
    public CLIFile getDefiningFile() {
        return _definingFile;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public boolean hasThis() {
        return _hasThis;
    }

    @Override
    public boolean hasExplicitThis() {
        return _hasExplicitType;
    }

    @Override
    public boolean hasVarArg() {
        return _hasVarArg;
    }

    @Override
    public boolean isVirtual() {
        return _isVirtual;
    }

    @Override
    public IParameter[] getParameters() {
        return _parameters;
    }

    @Override
    public IParameter[] getLocals() {
        return _locals;
    }
    @Override
    public IParameter getReturnType() {
        return _retType;
    }

    @Override
    public IParameter getThis() {
        return _this;
    }

    @Override
    public IExceptionHandler[] getExceptionHandlers() {
        return _exceptionHandlers;
    }

    @Override
    public IComponent getDefiningComponent() {
        return _definingComponent;
    }

    @Override
    public IType getDefiningType() {
        return _definingType;
    }
    @Override
    public IMethod getDefinition() {
        return this;
    }

    @Override
    public int getMaxStack()
    {
        return _maxStack;
    }

    @Override
    public String toString() {
        return "_ " + getName() + "()";
    }
    //endregion
}
