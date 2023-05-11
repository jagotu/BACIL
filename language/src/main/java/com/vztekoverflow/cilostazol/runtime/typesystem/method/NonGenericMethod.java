package com.vztekoverflow.cilostazol.runtime.typesystem.method;

import com.oracle.truffle.api.nodes.RootNode;
import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.nodes.CILOSTAZOLRootNode;
import com.vztekoverflow.cilostazol.runtime.typesystem.TypeSystemException;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitution;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ITypeParameter;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.exceptionhandler.IExceptionHandler;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.parameter.IParameter;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public class NonGenericMethod extends MethodBase implements ICILBasedMethod {
    private final byte[] _cil;

    public NonGenericMethod(CLIFile _definingFile, String _name, boolean _hasThis, boolean _hasExplicitType, boolean _hasVarArg, boolean _isVirtual, IParameter[] _parameters, IParameter[] _locals, IParameter _retType, IParameter _this, IExceptionHandler[] _exceptionHandlers, IComponent _definingComponent, IType _definingType, byte[] cil, int maxStack) {
        super(_definingFile, _name, _hasThis, _hasExplicitType, _hasVarArg, _isVirtual, _parameters, _locals, _retType, _this, _exceptionHandlers, _definingComponent, _definingType, maxStack);
        _cil = cil;
    }

    //region MethodBase
    @Override
    public ITypeParameter[] getTypeParameters() {
        throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.invalidOperation"));
    }

    @Override
    public IMethod substitute(ISubstitution<IType> substitution) {
        throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.invalidOperation"));
    }

    @Override
    public IMethod getConstructedFrom() {
        throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.invalidOperation"));
    }

    @Override
    public RootNode getNode() {
        if (_node == null)
            _node = CILOSTAZOLRootNode.create(this, _cil);

        return _node;
    }
    //endregion

    //region ICILBasedMethod
    @Override
    public byte[] getCIL() {
        return _cil;
    }
    //endregion
}
