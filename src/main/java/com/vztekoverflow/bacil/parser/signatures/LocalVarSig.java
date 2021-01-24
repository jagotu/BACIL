package com.vztekoverflow.bacil.parser.signatures;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.runtime.types.*;

import java.util.ArrayList;
import java.util.List;

public class LocalVarSig {

    private static final String TYPE = "LocalVarSig";

    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private final Type[] varTypes;

    public LocalVarSig(Type[] varTypes) {
        this.varTypes = varTypes;
    }

    public static LocalVarSig read(byte[] signature)
    {
        SignatureReader reader = new SignatureReader(signature);
        reader.assertUnsigned(7, TYPE);

        int count = reader.getUnsigned();
        Type[] varTypes = new Type[count];

        for(int i = 0; i < count; i++) {
            if (reader.peekUnsigned() == Type.ELEMENT_TYPE_TYPEDBYREF) {
                varTypes[i] = SimpleType.TYPEDBYREF;
                continue;
            }


            boolean pinned = false;
            boolean byRef = false;

            List<CustomMod> mods = CustomMod.readAll(reader);

            if (reader.peekUnsigned() == Type.ELEMENT_TYPE_PINNED)
            {
                pinned = true;
                reader.getUnsigned();
            }

            if (reader.peekUnsigned() == Type.ELEMENT_TYPE_BYREF)
            {
                byRef = true;
                reader.getUnsigned();
            }

            Type type = Type.create(reader);
            if(byRef)
            {
                type = new ByRefWrapped(type);
            }
            if(pinned)
            {
                type = new PinnedWrapped(type);
            }
            if(mods != null)
            {
                type = new CustomModWrapped(type, mods);
            }
            varTypes[i] = type;
        }

        return new LocalVarSig(varTypes);

    }
}
