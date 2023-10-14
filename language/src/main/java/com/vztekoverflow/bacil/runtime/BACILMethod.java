package com.vztekoverflow.bacil.runtime;

import com.oracle.truffle.api.CallTarget;
import com.vztekoverflow.bacil.parser.signatures.MethodDefSig;
import com.vztekoverflow.bacil.runtime.types.Type;

/**
 * A generic interface for callable BACIL methods.
 */
public interface BACILMethod {
    /**
     * Get the call target implementing this method.
     */
    public CallTarget getMethodCallTarget();

    /**
     * Get the return type of this method.
     */
    public Type getRetType();

    /**
     * Get the count of arguments of this method.
     */
    public int getArgsCount();

    /**
     * Get the count of local variables of this method.
     */
    int getVarsCount();

    /**
     * Get the types of all locations of this method (arguments + variables).
     */
    Type[] getLocationsTypes();

    /**
     * Get the type that defined this method.
     */
    public Type getDefiningType();

    /**
     * Get name of this method.
     */
    public String getName();

    /**
     * Get whether this method is a virtual method.
     */
    public boolean isVirtual();

    /**
     * Get signature of this method definition.
     */
    public MethodDefSig getSignature();
}
