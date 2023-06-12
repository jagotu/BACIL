package com.vztekoverflow.cilostazol.runtime.typesystem.type;

public enum TypeLayout {
  Auto,
  Sequential,
  Explicit;
  public static final int MASK = 0x18;

  public static TypeLayout fromFlags(int flags) {
    return TypeLayout.values()[(flags & MASK) >> 3];
  }
}
