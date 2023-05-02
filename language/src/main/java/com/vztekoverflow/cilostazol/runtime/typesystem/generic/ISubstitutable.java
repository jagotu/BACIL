package com.vztekoverflow.cilostazol.runtime.typesystem.generic;

public interface ISubstitutable<TSubstitutable> {
    public TSubstitutable substitute(ISubstitution substitution);
    public TSubstitutable getDefinition();
    public TSubstitutable getConstructedFrom();
}
