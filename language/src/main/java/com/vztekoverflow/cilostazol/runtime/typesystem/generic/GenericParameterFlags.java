package com.vztekoverflow.cilostazol.runtime.typesystem.generic;

public class GenericParameterFlags {
    public enum Flag
    {
        NODE(0x0000),
        COVARIANT(0x0001),
        CONTRAVARIANT(0x0002),
        REFERENCE_TYPE_CONSTRAINT(0x0004),
        NOT_NULLABLE_VALUE_TYPE_CONSTRAINT(0x0008),
        DEFAULT_CONSTRUCTOR_CONSTRAINT(0x0010);

        public final int code;
        Flag(int code)
        {
            this.code = code;
        }
    }

    //region Masks
    private static final int F_VARIANCE_MASK = 0x0003;
    private static final int F_SPECIAL_CONSTRAINT_MASK = 0x001C;
    //endregion

    public boolean hasFlag(Flag flag)
    {
        switch (flag)
        {
            case NODE:
                return ((_flags & F_VARIANCE_MASK) == flag.code);
            case COVARIANT:
            case CONTRAVARIANT:
                return !hasFlag(Flag.NODE) && ((_flags & F_VARIANCE_MASK) == flag.code);
            case REFERENCE_TYPE_CONSTRAINT:
            case NOT_NULLABLE_VALUE_TYPE_CONSTRAINT:
            case DEFAULT_CONSTRUCTOR_CONSTRAINT:
                return (_flags & F_SPECIAL_CONSTRAINT_MASK) == flag.code;
            default:
                return !hasFlag(Flag.NODE) && (_flags & flag.code) == flag.code;
        }
    }

    private final int _flags;

    public GenericParameterFlags(int flags)
    {
        _flags = flags;
    }
}
