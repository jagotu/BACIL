package com.vztekoverflow.cilostazol.runtime.typesystem.generic;

public interface ISubstitutable<TSubstitutable, TSubstitutedItem> {
  public TSubstitutable substitute(ISubstitution<TSubstitutedItem> substitution);

  public TSubstitutable getDefinition();

  public TSubstitutable getConstructedFrom();
}
