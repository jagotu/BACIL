package com.vztekoverflow.cilostazol.runtime.typesystem.method.flags;

public class MethodSectionFlags {
  public final int _flags;

  public MethodSectionFlags(int flags) {
    _flags = flags;
  }

  public boolean hasFlag(Flag flag) {
    return (_flags & flag.code) == flag.code;
  }

  public enum Flag {
    CORILMETHOD_SECT_EHTABLE(0x1),
    CORILMETHOD_SECT_OPTILTABLE(0x2),
    CORILMETHOD_SECT_FATFORMAT(0x40),
    CORILMETHOD_SECT_MORESECTS(0x80);

    public final int code;

    Flag(int code) {
      this.code = code;
    }
  }
}
