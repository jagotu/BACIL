package com.vztekoverflow.bacil;

import com.oracle.truffle.api.CompilerDirectives;

/**
 * A class for runtime errors in BACIL.
 */
// Long term TODO: use CompilerDirectives.shouldNotReachHere() in PE code,
//  it has a special treatment better than the pattern:
//
//  CompilerDirectives.transferToInterpreter();
//  throw new BACILInternalError();
public class BACILInternalError extends BACILException {
    public BACILInternalError(String message) {
        super(message);
    }
}
