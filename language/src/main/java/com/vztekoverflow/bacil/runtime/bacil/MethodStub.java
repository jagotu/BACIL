package com.vztekoverflow.bacil.runtime.bacil;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.vztekoverflow.bacil.parser.signatures.MethodDefSig;
import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.builtin.BuiltinTypes;

public class MethodStub extends JavaMethod {

    private final MethodDefSig signature;
    private final Type definingType;
    private final boolean isVoid;

    public MethodStub(BuiltinTypes builtinTypes, TruffleLanguage<?> language, MethodDefSig signature, Type definingType) {
        super(language);
        this.signature = signature;
        this.definingType = definingType;
        this.isVoid = signature.getRetType() == builtinTypes.getVoidType();
    }

    @Override
    public Object execute(VirtualFrame frame) {
        if(isVoid)
        {
            return null;
        } else {
            return definingType.initialValue();
        }
    }

    @Override
    public Type getRetType() {
        return signature.getRetType();
    }

    @Override
    public int getArgsCount() {
        return signature.getParamCount();
    }

    @Override
    public int getVarsCount() {
        return 0;
    }

    @Override
    public Type[] getLocationsTypes() {
        return signature.getParamTypes();
    }

    @Override
    public Type getDefiningType() {
        return definingType;
    }
}
