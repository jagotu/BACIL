package com.vztekoverflow.cilostazol.runtime.typesystem.method.flags;

public class MethodFlags {
  // region Masks
  private static final int F_MEMBER_ACCESS_MASK = 0x0007;
  private static final int F_V_TABLE_LAYOUT_MASK = 0x0100;
  public final int _flags;
  // endregion

  public MethodFlags(int flags) {
    _flags = flags;
  }

  public boolean hasFlag(Flag flag) {
    switch (flag) {
      case COMPILER_CONTROLLED:
      case PRIVATE:
      case FAM_AND_ASSEM:
      case ASSEM:
      case FAMILY:
      case FAM_OR_ASSEM:
      case PUBLIC:
        return (_flags & F_MEMBER_ACCESS_MASK) == flag.code;
      case REUSE_SLOT:
      case NEW_SLOT:
        return (_flags & F_V_TABLE_LAYOUT_MASK) == flag.code;
      default:
        return (_flags & flag.code) == flag.code;
    }
  }

  public enum Flag {
    COMPILER_CONTROLLED(0x0000),
    PRIVATE(0x0001),
    FAM_AND_ASSEM(0x0002),
    ASSEM(0x0003),
    FAMILY(0x0004),
    FAM_OR_ASSEM(0x0005),
    PUBLIC(0x0006),
    STATIC(0x0010),
    FINAL(0x0020),
    VIRTUAL(0x0040),
    HIDE_BY_SIG(0x0080),
    REUSE_SLOT(0x0000),
    NEW_SLOT(0x0100),
    STRICT(0x0200),
    ABSTRACT(0x0400),
    SPECIAL_NAME(0x0800),
    P_INVOKE_IMPL(0x2000),
    UNMANAGED_EXPORT(0x0000),
    RT_SPECIAL_NAME(0x1000),
    HAS_SECURITY(0x4000),
    REQUIRE_SEC_OBJECT(0x8000);

    public final int code;

    Flag(int code) {
      this.code = code;
    }
  }
}
