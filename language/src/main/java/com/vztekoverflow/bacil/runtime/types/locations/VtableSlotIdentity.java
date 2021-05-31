package com.vztekoverflow.bacil.runtime.types.locations;

import com.vztekoverflow.bacil.runtime.BACILMethod;

import java.util.Arrays;

public class VtableSlotIdentity {
    private final String name;
    private final byte[] signature;

    public VtableSlotIdentity(String name, byte[] signature) {
        this.name = name;
        this.signature = signature;
    }

    public String getName() {
        return name;
    }

    public byte[] getSignature() {
        return signature;
    }

    public boolean resolves(BACILMethod method)
    {
        return name.equals(method.getName()) &&
                Arrays.equals(method.getSignature(), signature);
    }
}
