package com.vztekoverflow.cilostazol.runtime.typesystem.field;

public enum FieldVisibility {
  CompilerControlled,
  Private,
  FamANDAssem,
  Assembly,
  Family,
  FamORAssem,
  Public;

  public static final int MASK = 0x7;

  public static FieldVisibility fromFlags(int flags) {
    return FieldVisibility.values()[flags & MASK];
  }
}
