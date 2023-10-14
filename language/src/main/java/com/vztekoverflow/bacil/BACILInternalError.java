package com.vztekoverflow.bacil;

/**
 * A class for runtime errors in BACIL.
 */
public class BACILInternalError extends BACILException {
    public BACILInternalError(String message) {
        super(message);
    }
}
