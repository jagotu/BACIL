package com.vztekoverflow.cilostazol.runtime.typesystem.generic;

import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

import java.util.HashMap;


public class Substitution<TItem> implements ISubstitution<TItem>{
    private final HashMap<TItem, TItem> _map;

    public Substitution(HashMap<TItem, TItem> map) {
        _map = map;
    }

    public Substitution(TItem[] from, TItem[] to)
    {
        _map = new HashMap<TItem, TItem>(from.length);
        for (int i = 0; i < from.length; i++) {
            _map.put(from[i], to[i]);
        }
    }

    @Override
    public TItem substitute(TItem type) {
        return _map.getOrDefault(type, type);
    }
}
