package com.vztekoverflow.cilostazol.runtime.typesystem.method;

import com.oracle.truffle.api.nodes.RootNode;
import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cil.parser.cli.signature.MethodDefFlags;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.exceptionhandler.IExceptionHandler;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.flags.MethodFlags;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.flags.MethodHeaderFlags;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.flags.MethodImplFlags;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.local.ILocal;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.parameter.IParameter;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.returnType.IReturnType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public abstract class MethodBase implements IMethod, ICILBasedMethod{
    protected final CLIFile _definingFile;
    protected final String _name;
    protected final IComponent _definingComponent;
    protected final IType _definingType;

    //Flags
    protected final MethodDefFlags _methodDefFlags;
    protected final MethodFlags _methodFlags;
    protected final MethodImplFlags _methodImplFlags;
    private  final MethodHeaderFlags _methodHeaderFlags;

    //Signature
    protected final IParameter[] _parameters;
    protected final ILocal[] _locals;
    protected final IReturnType _retType;
    protected final IExceptionHandler[] _exceptionHandlers;
    protected final byte[] _cil;

    //body
    protected final int _maxStack;
    protected RootNode _node;

    protected MethodBase(CLIFile definingFile, String name, IComponent definingComponent, IType definingType, MethodDefFlags methodDefFlags, MethodFlags methodFlags, MethodImplFlags methodImplFlags, MethodHeaderFlags methodHeaderFlags, IParameter[] parameters, ILocal[] locals, IReturnType retType, IExceptionHandler[] exceptionHandlers, byte[] cil, int maxStack) {
        _definingFile = definingFile;
        _name = name;
        _definingComponent = definingComponent;
        _definingType = definingType;
        _methodDefFlags = methodDefFlags;
        _methodFlags = methodFlags;
        _methodImplFlags = methodImplFlags;
        _methodHeaderFlags = methodHeaderFlags;
        _parameters = parameters;
        _locals = locals;
        _retType = retType;
        _exceptionHandlers = exceptionHandlers;
        _cil = cil;
        _maxStack = maxStack;
    }

    //region IMethod
    @Override
    public CLIFile getFile() {
        return _definingFile;
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
    public String getName() {
        return _name;
    }

    @Override
    public IParameter[] getParameters() {
        return _parameters;
    }

    @Override
    public ILocal[] getLocals() {
        return _locals;
    }

    @Override
    public IReturnType getReturnType() {
        return _retType;
    }

    @Override
    public IExceptionHandler[] getExceptionHandlers() {
        return _exceptionHandlers;
    }

    @Override
    public int getMaxStack() {
        return _maxStack;
    }

    @Override
    public IMethod getDefinition() {
        return this;
    }

    @Override
    public byte[] getCIL() {
        return _cil;
    }

    @Override
    public String toString() {
        return "_ " + getName() + "()";
    }
    //endregion
}
