package com.vztekoverflow.bacil.parser.signatures;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.runtime.types.Type;

public class MethodDefSig {

    private final boolean hasThis;
    private final boolean explicitThis;
    private final byte callingConvention;
    private final int genParamCount;
    private final Type retType;

    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private final Type[] paramTypes;

    public MethodDefSig(boolean hasThis, boolean explicitThis, byte callingConvention, int genParamCount, Type retType, Type[] paramTypes) {
        this.hasThis = hasThis;
        this.explicitThis = explicitThis;
        this.callingConvention = callingConvention;
        this.genParamCount = genParamCount;
        this.retType = retType;
        this.paramTypes = paramTypes;
    }

    private static final int HASTHIS = 0x20;
    private static final int EXPLICITTHIS = 0x40;
    public static final int DEFAULT = 0x0;
    public static final int VARARG = 0x5;
    public static final int GENERIC = 0x10;


    public static MethodDefSig read(byte[] signature) {
        SignatureReader reader = new SignatureReader(signature);
        int callingConvention = reader.getUnsigned();

        boolean hasThis = false;
        if((callingConvention & HASTHIS) != 0)
        {
            hasThis = true;
        }

        boolean explicitThis = false;
        if((callingConvention & EXPLICITTHIS) != 0)
        {
            explicitThis = true;
        }

        callingConvention = callingConvention & (~0x60);

        int genParamCount = -1;
        if(callingConvention == GENERIC)
        {
            genParamCount = reader.getUnsigned();
        }

        final int count = reader.getUnsigned();
        final Type retType = Type.readParam(reader, true);
        final Type[] paramTypes = new Type[count];
        for(int i = 0; i < count; i++)
        {
            paramTypes[i] = Type.readParam(reader, false);
        }

        return new MethodDefSig(hasThis, explicitThis, (byte)callingConvention, genParamCount, retType, paramTypes);

    }

    public int getParamCount()
    {
        return paramTypes.length;
    }

    public boolean isHasThis() {
        return hasThis;
    }

    public boolean isExplicitThis() {
        return explicitThis;
    }

    public byte getCallingConvention() {
        return callingConvention;
    }

    public int getGenParamCount() {
        return genParamCount;
    }

    public Type getRetType() {
        return retType;
    }

    public Type[] getParamTypes() {
        return paramTypes;
    }
}
