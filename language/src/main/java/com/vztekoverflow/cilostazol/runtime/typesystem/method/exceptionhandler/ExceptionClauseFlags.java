package com.vztekoverflow.cilostazol.runtime.typesystem.method.exceptionhandler;

public class ExceptionClauseFlags {
    public enum Flag
    {
        COR_ILEXCEPTION_CLAUSE_EXCEPTION (0x0),
        COR_ILEXCEPTION_CLAUSE_FILTER(0x1),
        COR_ILEXCEPTION_CLAUSE_FINALLY(0x2),
        COR_ILEXCEPTION_CLAUSE_FAULT(0x4);

        public final int code;
        Flag(int code)
        {
            this.code = code;
        }
    }

    public boolean hasFlag(Flag flag)
    {
        return _flags == flag.code;
    }

    public final int _flags;

    public ExceptionClauseFlags(int flags)
    {
        _flags = flags;
    }
}
