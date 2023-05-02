package com.vztekoverflow.cilostazol.runtime.typesystem.method;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitution;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public class SubstitutedGenericMethod implements IMethod {
    protected IMethod _definition;
    protected IMethod _constructedFrom;
    protected ISubstitution<IType> _substitution;
    public SubstitutedGenericMethod(IMethod _constructedFrom, IMethod _definition, ISubstitution<IType> substitution)
    {
        this._definition = _definition;
        this._constructedFrom = _constructedFrom;
        this._substitution = substitution;
    }

    //region IMethod
    @Override
    public CLIFile getDefiningFile() {
        return _constructedFrom.getDefiningFile();
    }

    @Override
    public String getName() {
        return _definition.getName();
    }

    @Override
    public boolean hasThis() {
        return _definition.hasThis();
    }

    @Override
    public boolean hasExplicitThis() {
        return _definition.hasExplicitThis();
    }

    @Override
    public boolean hasVarArg() {
        return _definition.hasVarArg();
    }

    @Override
    public boolean isVirtual() {
        return _definition.isVirtual();
    }

    @Override
    public IParameter[] getParameters() {
        throw new NotImplementedException();
    }

    @Override
    public IParameter[] getLocals() {
        throw new NotImplementedException();
    }

    @Override
    public ITypeParameter[] getTypeParameters() {
        throw new NotImplementedException();
    }

    @Override
    public IParameter getReturnType() {
        throw new NotImplementedException();
    }

    @Override
    public IParameter getThis() {
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
    public IMethod substitute(ISubstitution<IType> substitution) {
        return new SubstitutedGenericMethod(_definition, this, substitution);
    }

    @Override
    public IMethod getDefinition() {
        return _definition;
    }

    @Override
    public IMethod getConstructedFrom() {
        return _constructedFrom;
    }
    //endregion
}
