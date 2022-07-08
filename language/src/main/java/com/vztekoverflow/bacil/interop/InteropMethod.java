package com.vztekoverflow.bacil.interop;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.vztekoverflow.bacil.runtime.BACILMethod;

@ExportLibrary(InteropLibrary.class)
public class InteropMethod implements TruffleObject {

    private final BACILMethod method;

    public InteropMethod(BACILMethod method) {
        this.method = method;
    }

    @ExportMessage
    boolean isExecutable()
    {
        return true;
    }

    @ExportMessage
    Object execute(Object... args)
    {
        return method.getMethodCallTarget().call(args);
    }
}
