package com.vztekoverflow.bacil.runtime.bacil;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.builtin.BuiltinTypes;

public class BACILTickCountMethod extends JavaMethod {

    private final Type retType;

    private final Type definingType;

    public BACILTickCountMethod(BuiltinTypes builtinTypes, TruffleLanguage<?> language, Type definingType) {
        super(language);
        retType = builtinTypes.getInt64Type();
        this.definingType = definingType;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return System.currentTimeMillis();
    }

    @Override
    public Type getRetType() {
        return retType;
    }

    @Override
    public int getArgsCount() {
        return 0;
    }

    @Override
    public int getVarsCount() {
        return 0;
    }

    @Override
    public Type[] getLocationsTypes() {
        return new Type[0];
    }

    @Override
    public Type getDefiningType() {
        return definingType;
    }

    @Override
    public String getName() {
        return "TickCount";
    }

}
