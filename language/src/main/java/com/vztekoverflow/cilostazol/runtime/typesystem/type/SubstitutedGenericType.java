package com.vztekoverflow.cilostazol.runtime.typesystem.type;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.field.IField;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitution;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.IMethod;

public class SubstitutedGenericType implements IType {
    protected final IType _constructedFrom;
    protected final IType _definition;
    protected final ISubstitution _substitution;

    public SubstitutedGenericType(IType _constructedFrom, IType _definition, ISubstitution substitution) {
        this._constructedFrom = _constructedFrom;
        this._definition = _definition;
        this._substitution = substitution;
    }

    //region IType
    @Override
    public CLIFile getDefiningFile() {
        return _constructedFrom.getDefiningFile();
    }

    @Override
    public IType[] getTypeParameters() {

        throw new NotImplementedException();
    }

    @Override
    public String getName() {
        return _definition.getName();
    }

    @Override
    public String getNamespace() {
        return _definition.getNamespace();
    }

    @Override
    public IType getDirectBaseClass() {
        throw new NotImplementedException();
    }

    @Override
    public IType[] getInterfaces() {
        throw new NotImplementedException();
    }

    @Override
    public IMethod[] getMethods() {
        throw new NotImplementedException();
    }

    @Override
    public IMethod[] getVTable() {
        throw new NotImplementedException();
    }

    @Override
    public IField[] getFields() {
        throw new NotImplementedException();
    }

    @Override
    public IComponent getDefiningComponent() {
        return _constructedFrom.getDefiningComponent();
    }

    @Override
    public IType substitute(ISubstitution substitution) {
        return new SubstitutedGenericType(this, _definition, substitution);
    }

    @Override
    public IType getDefinition() {
        return _definition;
    }

    @Override
    public IType getConstructedFrom() {
        return _constructedFrom;
    }
    //endregion
}
