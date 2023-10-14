package com.vztekoverflow.bacil.runtime.bacil;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.vztekoverflow.bacil.parser.signatures.MethodDefSig;
import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.builtin.BuiltinTypes;

/**
 * Used for stubbing methods that use unimplemented features, making them return 0/null.
 */
public class MethodStub extends JavaMethod {

    private final MethodDefSig signature;
    private final Type definingType;
    private final boolean isVoid;
    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private final Type[] args;

    public MethodStub(BuiltinTypes builtinTypes, TruffleLanguage<?> language, MethodDefSig signature, Type definingType) {
        super(language);
        this.signature = signature;
        this.definingType = definingType;
        this.isVoid = signature.getRetType() == builtinTypes.getVoidType();

        if (signature.isHasThis() && !signature.isExplicitThis())
        {
            args = new Type[signature.getParamCount() +1];
            args[0] = definingType.getThisType();

            System.arraycopy(signature.getParamTypes(), 0, args, 1, signature.getParamCount());
        } else {
            args = signature.getParamTypes();
        }
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
        return args.length;
    }

    @Override
    public int getVarsCount() {
        return 0;
    }

    @Override
    public Type[] getLocationsTypes() {
        return args;
    }

    @Override
    public Type getDefiningType() {
        return definingType;
    }
}
