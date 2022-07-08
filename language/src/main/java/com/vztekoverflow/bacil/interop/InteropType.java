package com.vztekoverflow.bacil.interop;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.vztekoverflow.bacil.parser.signatures.MethodDefSig;
import com.vztekoverflow.bacil.runtime.types.Type;

@ExportLibrary(InteropLibrary.class)
public class InteropType implements TruffleObject {

    private class UniversalSignature extends MethodDefSig {
        public UniversalSignature() {
            super(false, false, (byte) 0, -1, null, null);
        }

        @Override
        public boolean compatibleWith(MethodDefSig other) {
            return true;
        }
    };

    private final Type type;
    private final MethodDefSig UNIVERSAL_SIGNATURE = new UniversalSignature();

    // ONLY SUPPORT CALLING METHODS
    public InteropType(Type type) {
        this.type = type;
    }

    @ExportMessage
    Object readMember(String member) {
        return new InteropMethod(type.getMemberMethod(member, UNIVERSAL_SIGNATURE));
    }

    @ExportMessage final boolean isMemberInvocable(String member) {
        return type.getMemberMethod(member, UNIVERSAL_SIGNATURE) != null;
    }

    @ExportMessage
    public Object invokeMember(String member, Object... arguments)
    {
        return type.getMemberMethod(member, UNIVERSAL_SIGNATURE).getMethodCallTarget().call(arguments);
    }

    @ExportMessage
    boolean isMemberReadable(String member) {
        return type.getMemberMethod(member, UNIVERSAL_SIGNATURE) != null;
    }

    @ExportMessage
    boolean hasMembers() {
        return true;
    }

    @ExportMessage
    Object getMembers(boolean includeInternal) {
        //TODO Type currently doesn't expose listings, juts getters
        return EmptyArray.INSTANCE;
    }

}
