package com.vztekoverflow.cilostazol.runtime.symbols.utils;

public enum FieldSymbolVisibility {
  CompilerControlled,
  Private,
  FamANDAssem,
  Assembly,
  Family,
  FamORAssem,
  Public;

  public static final int MASK = 0x7;

  public static FieldSymbolVisibility fromFlags(int flags) {
    return FieldSymbolVisibility.values()[flags & MASK];
  }
}
