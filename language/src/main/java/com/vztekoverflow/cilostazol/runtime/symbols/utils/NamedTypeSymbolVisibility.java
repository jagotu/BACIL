package com.vztekoverflow.cilostazol.runtime.symbols.utils;

public enum NamedTypeSymbolVisibility {
  NotPublic,
  Public,
  NestedPublic,
  NestedPrivate,
  NestedFamily,
  NestedAssembly,
  NestedFamANDAssem,
  NestedFamORAssem;
  public static final int MASK = 0x7;

  public static NamedTypeSymbolVisibility fromFlags(int flags) {
    return NamedTypeSymbolVisibility.values()[flags & MASK];
  }
}
