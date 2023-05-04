package com.vztekoverflow.cilostazol.runtime.typesystem.type;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.field.IField;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitution;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.IMethod;

import java.util.Arrays;

public class SubstitutedGenericType implements IType {
    protected final IType _constructedFrom;
    protected final IType _definition;
    protected final ISubstitution<IType> _substitution;

    public SubstitutedGenericType(IType _constructedFrom, IType _definition, ISubstitution<IType> substitution) {
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
        return (IType[])Arrays.stream(_constructedFrom.getTypeParameters()).map(x -> x.substitute(_substitution)).toArray();
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
        return _constructedFrom.getDirectBaseClass().substitute(_substitution);
    }

    @Override
    public IType[] getInterfaces() {
        return (IType[]) Arrays.stream(_constructedFrom.getInterfaces()).map(x -> x.substitute(_substitution)).toArray();
    }

    @Override
    public IMethod[] getMethods() {
        return (IMethod[])Arrays.stream(_constructedFrom.getMethods()).map(x -> x.substitute(_substitution)).toArray();
    }

    @Override
    public IMethod[] getVTable() {
        return (IMethod[])Arrays.stream(_constructedFrom.getVTable()).map(x -> x.substitute(_substitution)).toArray();
    }

    @Override
    public IField[] getFields() {
        return (IField[]) Arrays.stream(_constructedFrom.getFields()).map(x -> x.substitute(_substitution)).toArray();
    }

    @Override
    public IComponent getDefiningComponent() {
        return _constructedFrom.getDefiningComponent();
    }

    @Override
    public IType substitute(ISubstitution<IType> substitution) {
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
