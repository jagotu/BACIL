package com.vztekoverflow.bacil.runtime.bacil;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.builtin.BuiltinTypes;

public class BACILConsoleWriteMethod extends JavaMethod {

    private final Type retType;

    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private final Type[] argTypes;

    private final Type definingType;

    public BACILConsoleWriteMethod(BuiltinTypes builtinTypes, TruffleLanguage<?> language, Type definingType) {
        super(language);
        retType = builtinTypes.getVoidType();
        argTypes = new Type[] {builtinTypes.getObjectType()};
        this.definingType = definingType;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        System.out.print(frame.getArguments()[0]);
        return null;
    }

    @Override
    public Type getRetType() {
        return retType;
    }

    @Override
    public int getArgsCount() {
        return 1;
    }

    @Override
    public int getVarsCount() {
        return 0;
    }

    @Override
    public Type[] getLocationsTypes() {
        return argTypes;
    }

    @Override
    public Type getDefiningType() {
        return definingType;
    }

    @Override
    public String getName() {
        return "Write";
    }

}
