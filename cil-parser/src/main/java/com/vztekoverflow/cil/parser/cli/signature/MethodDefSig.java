package com.vztekoverflow.cil.parser.cli.signature;

import com.vztekoverflow.cil.parser.cli.CLIFile;

public class MethodDefSig {
    //region Constants
    private static final int HAS_THIS = 0x20;
    private static final int EXPLICIT_THIS = 0x40;
    private static final int DEFAULT = 0x0;
    private static final int VARARG = 0x5;
    private static final int GENERIC = 0x10;
    //endregion

    private final boolean _hasThis;
    private final boolean _hasExplicitThis;
    private final boolean _hasVarArg;
    private final int _genParamCount;
    private final ParamSig _retType;
    private final ParamSig[] _params;


    public MethodDefSig(boolean hasThis, boolean hasExplicitThis, boolean hasVarArg, int _genParamCount, ParamSig _retType, ParamSig[] _params) {
        this._hasThis = hasThis;
        this._hasExplicitThis = hasExplicitThis;
        this._hasVarArg = hasVarArg;
        this._genParamCount = _genParamCount;
        this._retType = _retType;
        this._params = _params;
    }

    public static MethodDefSig parse(SignatureReader reader, CLIFile file) {
        int callingConvention = reader.getUnsigned();

        boolean hasThis = (callingConvention & HAS_THIS) != 0;
        boolean hasExplicitThis = (callingConvention & EXPLICIT_THIS) != 0;
        boolean hasVarArg = (callingConvention & VARARG) != 0;

        int genParamCount = -1;
        if((callingConvention & GENERIC) != 0)
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

        return new MethodDefSig(hasThis, hasExplicitThis, hasVarArg, genParamCount, retType, paramTypes);
    }


    public boolean hasThis() {
        return _hasThis;
    }

    public boolean hasExplicitThis() {
        return _hasExplicitThis;
    }

    public boolean hasVararg() { return _hasVarArg;}

    public int getGenParamCount() {
        return _genParamCount;
    }
    public ParamSig[] getParams() {return  _params; }
    public ParamSig getRetType() {return  _retType; }
}
