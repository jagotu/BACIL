package com.vztekoverflow.bacil.runtime.bacil;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.builtin.BuiltinTypes;

/**
 * Implementation of the BACILHelpers.BACILEnvironment.GetTicks() method.
 *
 * Returns the amount of ticks since BACILHelpers.BACILEnvironment.StartTimer() was called.
 */
public class BACILGetTicksMethod extends JavaMethod {

    private final Type retType;

    private final BACILEnvironmentType definingType;

    public BACILGetTicksMethod(BuiltinTypes builtinTypes, TruffleLanguage<?> language, BACILEnvironmentType definingType) {
        super(language);
        retType = builtinTypes.getInt64Type();
        this.definingType = definingType;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return System.nanoTime() - definingType.timerStart;
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
        return "GetTicks";
    }

}
