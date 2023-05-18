package com.vztekoverflow.cilostazol.runtime.typesystem.method.flags;

public class MethodHeaderFlags {
    public enum Flag
    {
        CORILMETHOD_TINYFORMAT(0x2),
        CORILMETHOD_FATFORMAT(0x3),
        CORILMETHOD_INITLOCALS(0x10),
        CORILMETHOD_MORESECTS(0x8);

        public final int code;
        Flag(int code)
        {
            this.code = code;
        }
    }

    //region Masks
    private final static int F_CORILMETHOD_FORMAT_MASK = 0x3;
    //endregion

    public boolean hasFlag(Flag flag)
    {
        switch (flag)
        {
            case CORILMETHOD_TINYFORMAT:
            case CORILMETHOD_FATFORMAT:
                return (_flags & F_CORILMETHOD_FORMAT_MASK) == flag.code;
            default:
                return (_flags & flag.code) == flag.code;
        }
    }

    public final int _flags;

    public MethodHeaderFlags(int flags)
    {
        _flags = flags;
    }
}
