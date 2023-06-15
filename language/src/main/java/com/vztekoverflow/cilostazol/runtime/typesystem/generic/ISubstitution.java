package com.vztekoverflow.cilostazol.runtime.typesystem.generic;

public interface ISubstitution<TItem> {
  public TItem substitute(TItem type);
}
