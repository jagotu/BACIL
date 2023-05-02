package com.vztekoverflow.cilostazol.runtime.typesystem.generic;

import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

import java.util.HashMap;


public class Substitution implements ISubstitution{
    private final HashMap<IType, IType> _map;

    public Substitution(HashMap<IType, IType> map) {
        _map = map;
    }

    @Override
    public IType substitute(IType type) {
        return _map.getOrDefault(type, null);
    }
}
