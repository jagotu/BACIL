package com.vztekoverflow.cil.parser.cli.signature;

import com.vztekoverflow.cil.parser.cli.CLIFile;

public class MethodDefSig {
    private static final int HASTHIS = 0x20;
    private static final int EXPLICITTHIS = 0x40;
    public static final int DEFAULT = 0x0;
    public static final int VARARG = 0x5;
    public static final int GENERIC = 0x10;

    public MethodDefSig(boolean hasThis, boolean explicitThis, byte callingConvention, int genParamCount, ParamSig retType, ParamSig[] params) {
        this.hasThis = hasThis;
        this.explicitThis = explicitThis;
        this.callingConvention = callingConvention;
        this.genParamCount = genParamCount;
        this.retType = retType;
        this.params = params;
    }

    public static MethodDefSig parse(SignatureReader reader, CLIFile file) {
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
        final ParamSig retType = ParamSig.parse(reader, file, true);
        final ParamSig[] paramTypes = new ParamSig[count];
        for(int i = 0; i < count; i++)
        {
            paramTypes[i] = ParamSig.parse(reader, file, false);
        }

        return new MethodDefSig(hasThis, explicitThis, (byte)callingConvention, genParamCount, retType, paramTypes);
    }

    private final boolean hasThis;
    private final boolean explicitThis;
    private final byte callingConvention;
    private final int genParamCount;
    private final ParamSig retType;
    private final ParamSig[] params;
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
    public int getParamsCount() {return  params.length ;}
}
