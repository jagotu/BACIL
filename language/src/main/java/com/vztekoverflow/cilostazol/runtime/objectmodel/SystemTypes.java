package com.vztekoverflow.cilostazol.runtime.objectmodel;


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

  // TODO: It should rely on Assembly as well...
  public static SystemTypes getTypeKind(String name, String namespace) {
    if (Objects.equals(namespace, "System")) {
      switch (name) {
        case "Boolean":
          return SystemTypes.Boolean;
        case "Byte":
        case "SByte":
        case "Char":
          return SystemTypes.Char;
        case "Int16":
        case "UInt16":
        case "Int32":
        case "UInt32":
          return SystemTypes.Int;
        case "Double":
          return SystemTypes.Double;
        case "Single":
          return SystemTypes.Float;
        case "Int64":
        case "UInt64":
          return SystemTypes.Long;
        // Decimal, UIntPtr, IntPtr ??
      }
    }

    return SystemTypes.Object;
  }
}
