package com.vztekoverflow.cilostazol.runtime.typesystem.method.flags;

public class MethodImplFlags {
    public enum Flag
    {
        IL(0x0000),
        NATIVE(0x0001),
        OPTIL(0x0002),
        RUNTIME(0x0003),
        UNMANAGED(0x0004),
        MANAGED(0x0000),
        FORWARD_REF(0x0010),
        PRESERVE_SIG(0x0080),
        INTERNAL_CALL(0x1000),
        SYNCHRONIZED(0x0020),
        NO_INLINING(0x0008),
        MAX_METHOD_IMPL_VAL(0xFFFF),
        NO_OPTIMIZATION(0x0040);


        public final int code;
        Flag(int code)
        {
            this.code = code;
        }
    }

    //region Masks
    private static final int F_CODE_TYPE_MASK = 0x0003;
    private static final int F_MANAGED_MASK = 0x0004;
    //endregion

    public boolean hasFlag(Flag flag)
    {
        switch (flag)
        {
            case IL:
            case NATIVE:
            case OPTIL:
            case RUNTIME:
                return (_flags & F_CODE_TYPE_MASK) == flag.code;
            case UNMANAGED:
            case MANAGED:
                return (_flags & F_MANAGED_MASK) == flag.code;
            default:
                return (_flags & flag.code) == flag.code;
        }
    }

    public final int _flags;

    public MethodImplFlags(int flags) {
        _flags = flags;
    }
}
