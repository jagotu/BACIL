package com.vztekoverflow.cilostazol.runtime.typesystem.method.parameter;

public class ParamFlags {
  private final int _flags;

  public ParamFlags(int flags) {
    _flags = flags;
  }

  public boolean hasFlag(Flag flag) {
    return (_flags & flag.code) == flag.code;
  }

  public enum Flag {
    IN(0x0001),
    OUT(0x0002),
    OPTIONAL(0x0010),
    HAS_DEFAULT(0x1000),
    HAS_FIELD_MARSHAL(0x2000),
    UNUSED(0xCFE0);

    public final int code;

    Flag(int code) {
      this.code = code;
    }
  }
}
