package com.vztekoverflow.bacil.runtime;

import com.oracle.truffle.api.CallTarget;
import com.vztekoverflow.bacil.runtime.types.Type;

public interface BACILMethod {
    public CallTarget getCallTarget();

    public Type getRetType();

    public int getArgsCount();

    int getVarsCount();

    Type[] getLocationsTypes();

    public Type getDefiningType();
}
