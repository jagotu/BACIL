package com.vztekoverflow.cilostazol.runtime.typesystem.type;

public enum TypeVisibility {
  NotPublic,
  Public,
  NestedPublic,
  NestedPrivate,
  NestedFamily,
  NestedAssembly,
  NestedFamANDAssem,
  NestedFamORAssem;
  public static final int MASK = 0x7;

  public static TypeVisibility fromFlags(int flags) {
    return TypeVisibility.values()[flags & MASK];
  }
}
