package com.vztekoverflow.cilostazol.runtime.typesystem.generic;

public class GenericParameterFlags {
  // region Masks
  private static final int F_VARIANCE_MASK = 0x0003;
  private static final int F_SPECIAL_CONSTRAINT_MASK = 0x001C;
  private final int _flags;
  // endregion

  public GenericParameterFlags(int flags) {
    _flags = flags;
  }

  public boolean hasFlag(Flag flag) {
    switch (flag) {
      case NONE:
        return ((_flags & F_VARIANCE_MASK) == flag.code);
      case COVARIANT:
      case CONTRAVARIANT:
        return !hasFlag(Flag.NONE) && ((_flags & F_VARIANCE_MASK) == flag.code);
      case REFERENCE_TYPE_CONSTRAINT:
      case NOT_NULLABLE_VALUE_TYPE_CONSTRAINT:
      case DEFAULT_CONSTRUCTOR_CONSTRAINT:
        return (_flags & F_SPECIAL_CONSTRAINT_MASK) == flag.code;
      default:
        return !hasFlag(Flag.NONE) && (_flags & flag.code) == flag.code;
    }
  }

  public enum Flag {
    NONE(0x0000),
    COVARIANT(0x0001),
    CONTRAVARIANT(0x0002),
    REFERENCE_TYPE_CONSTRAINT(0x0004),
    NOT_NULLABLE_VALUE_TYPE_CONSTRAINT(0x0008),
    DEFAULT_CONSTRUCTOR_CONSTRAINT(0x0010);

    public final int code;

    Flag(int code) {
      this.code = code;
    }
  }
}
