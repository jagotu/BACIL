package com.vztekoverflow.cilostazol.runtime.typesystem.generic;

import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

import java.util.HashMap;


public class Substitution<TItem> implements ISubstitution<TItem>{
    private final HashMap<TItem, TItem> _map;

    public Substitution(HashMap<TItem, TItem> map) {
        _map = map;
    }

    @Override
    public TItem substitute(TItem type) {
        return _map.getOrDefault(type, null);
    }
}
