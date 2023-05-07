package com.vztekoverflow.cilostazol.runtime.typesystem.method;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.nodes.CILOSTAZOLRootNode;
import com.vztekoverflow.cilostazol.runtime.typesystem.TypeSystemException;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitution;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ITypeParameter;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public class OpenGenericMethod extends MethodBase implements ICILBasedMethod {
    protected final ITypeParameter[] _typeParameters;
    protected final byte[] _cil;
    public OpenGenericMethod(CLIFile _definingFile,
                             String _name,
                             boolean _hasThis,
                             boolean _hasExplicitType,
                             boolean _hasVarArg,
                             boolean _isVirtual,
                             IParameter[] _parameters,
                             IParameter[] _locals,
                             ITypeParameter[] _typeParameters,
                             IParameter _retType,
                             IParameter _this,
                             IExceptionHandler[] _exceptionHandlers,
                             IComponent _definingComponent,
                             IType _definingType,
                             byte[] cil,
                             int maxStack) {
        super(_definingFile, _name, _hasThis, _hasExplicitType, _hasVarArg, _isVirtual, _parameters, _locals, _retType, _this, _exceptionHandlers, _definingComponent, _definingType, maxStack);
        this._typeParameters = _typeParameters;
        _cil = cil;
    }

    //region MethodBase
    @Override
    public ITypeParameter[] getTypeParameters() {
        return _typeParameters;
    }

    @Override
    public IMethod substitute(ISubstitution<IType> substitution) {
        return new SubstitutedGenericMethod(this, this, substitution);
    }

    @Override
    public IMethod getConstructedFrom() {
        return this;
    }

    @Override
    public CILOSTAZOLRootNode getNode() {
        throw new TypeSystemException("cilostazol.exception.invalidOperation");
    }
    //endregion

    //region ICILBasedMethod
    @Override
    public byte[] getCIL() {
        return _cil;
    }
    //endregion
}
