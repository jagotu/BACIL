package com.vztekoverflow.cilostazol.meta;

import java.util.Objects;

public enum SystemTypes {
  Boolean,
  Char,
  Int,
  Float,
  Long,
  Double,
  Void,
  Object;

  // TODO: It should rely on assembly identity too.
  public static SystemTypes getKind(String name, String namespace) {
    if (Objects.equals(namespace, "System")) {
      switch (name) {
        case "Boolean":
          return SystemTypes.Boolean;
        case "Byte":
        case "SByte":
        case "Char":
          return SystemTypes.Char;
        case "Double":
          return SystemTypes.Double;
        case "Single":
          return SystemTypes.Float;
        case "Int16":
        case "UInt16":
        case "Int32":
        case "UInt32":
          return SystemTypes.Int;
        case "Int64":
        case "UInt64":
          return SystemTypes.Long;
          // Decimal, UIntPtr, IntPtr ??
      }
    }

    return SystemTypes.Object;
  }
}
