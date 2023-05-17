package com.vztekoverflow.cilostazol.runtime.typesystem.method;

import com.oracle.truffle.api.nodes.RootNode;
import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;
import com.vztekoverflow.cilostazol.nodes.CILOSTAZOLRootNode;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitution;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ITypeParameter;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.exceptionhandler.IExceptionHandler;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.local.ILocal;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.parameter.IParameter;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.returnType.IReturnType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public class SubstitutedGenericMethod implements IMethod, ICILBasedMethod {
    protected final IMethod _definition;
    protected final ICILBasedMethod _constructedFrom;
    protected final ISubstitution<IType> _substitution;
    protected RootNode _node;
    public SubstitutedGenericMethod(ICILBasedMethod _constructedFrom, IMethod _definition, ISubstitution<IType> substitution)
    {
        this._definition = _definition;
        this._constructedFrom = _constructedFrom;
        this._substitution = substitution;
        _node = null;
    }

    //region IMethod
    @Override
    public String getName() {
        return _definition.getName();
    }

    @Override
    public CLIFile getFile() {
        return _constructedFrom.getFile();
    }

    @Override
    public IParameter[] getParameters() {
        throw new NotImplementedException();
    }

    @Override
    public ILocal[] getLocals() {
        throw new NotImplementedException();
    }

    @Override
    public ITypeParameter[] getTypeParameters() {
        throw new NotImplementedException();
    }

    @Override
    public IReturnType getReturnType() {
        throw new NotImplementedException();
    }

    @Override
    public IExceptionHandler[] getExceptionHandlers() {
        throw new NotImplementedException();
    }

    @Override
    public IComponent getDefiningComponent() {
        return _constructedFrom.getDefiningComponent();
    }

    @Override
    public IType getDefiningType() {
        return _definition.getDefiningType();
    }

    @Override
    public int getMaxStack() {
        return _definition.getMaxStack();
    }

    @Override
    public IMethod substitute(ISubstitution<IType> substitution) {
        return new SubstitutedGenericMethod(this, _definition, substitution);
    }

    @Override
    public IMethod getDefinition() {
        return _definition;
    }

    @Override
    public IMethod getConstructedFrom() {
        return _constructedFrom;
    }

    @Override
    public RootNode getNode() {
        if (_node == null)
            _node = CILOSTAZOLRootNode.create(this, _constructedFrom.getCIL());

        return _node;
    }
    //endregion

    //region ICILBasedMethod
    @Override
    public byte[] getCIL() {
        return _constructedFrom.getCIL();
    }
    //endregion
}
