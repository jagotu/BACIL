package com.vztekoverflow.cilostazol.runtime.typesystem.type.builtin.system;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.CLIComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitution;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public class BooleanType extends SystemBaseType {
    public BooleanType(CLIFile _definingFile, IType _directBaseClass, IType[] _interfaces, CLIComponent _definingComponent) {
        super(_definingFile, "Boolean", "System", _directBaseClass, _interfaces, _definingComponent);
    }

    @Override
    public IType[] getTypeParameters() {
        return new IType[0];
    }

    @Override
    public IType substitute(ISubstitution<IType> substitution) {
        return null;
    }

    @Override
    public IType getConstructedFrom() {
        return null;
    }
}
