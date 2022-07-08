package com.vztekoverflow.bacil.interop;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.vztekoverflow.bacil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.bacil.runtime.BACILContext;

@ExportLibrary(InteropLibrary.class)
public final class BACILBindings implements TruffleObject {

    private final BACILContext context;

    public BACILBindings(BACILContext context) {
        this.context = context;
    }


    @ExportMessage
    Object readMember(String member) {
        return new InteropComponent(context.getAssembly(new AssemblyIdentity((short) -1, (short) -1, (short) -1, (short) -1, member)));
    }

    @ExportMessage
    boolean hasMembers() {
        return true;
    }

    @ExportMessage
    boolean isMemberReadable(String member) {
        return context.getAssembly(new AssemblyIdentity((short) -1, (short) -1, (short) -1, (short) -1, member)) != null;
    }

    @ExportMessage
    Object getMembers(boolean includeInternal) {
        return EmptyArray.INSTANCE;
    }


}
