package com.vztekoverflow.cilostazol.runtime.symbols.utils;

public enum NamedTypeSymbolSemantics {
  Class,
  Interface;
  public static final int MASK = 0x20;

  public static NamedTypeSymbolSemantics fromFlags(int flags) {
    return NamedTypeSymbolSemantics.values()[(flags & MASK) >> 5];
  }
}
