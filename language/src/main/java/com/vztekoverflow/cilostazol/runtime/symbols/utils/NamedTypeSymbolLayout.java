package com.vztekoverflow.cilostazol.runtime.symbols.utils;

public enum NamedTypeSymbolLayout {
  Auto,
  Sequential,
  Explicit;
  public static final int MASK = 0x18;

  public static NamedTypeSymbolLayout fromFlags(int flags) {
    return NamedTypeSymbolLayout.values()[(flags & MASK) >> 3];
  }
}
