package com.vztekoverflow.cil.parser.cli.signature;

public class MethodDefFlags {
  private final int _flags;

  public MethodDefFlags(int flags) {
    _flags = flags;
  }

  public boolean hasFlag(Flag flag) {
    return (_flags & flag.code) == flag.code;
  }

  public enum Flag {
    HAS_THIS(0x20),
    EXPLICIT_THIS(0x40),
    DEFAULT(0x0),
    VARARG(0x5),
    GENERIC(0x10);

    public final int code;

    Flag(int code) {
      this.code = code;
    }
  }
}
