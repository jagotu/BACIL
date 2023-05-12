package com.vztekoverflow.cil.parser.cli.signature;

import java.util.ArrayList;
import java.util.List;

public class FieldSig {
    //region Constants
    private static final int FIELD = 0x6;
    private static final int ELEMENT_TYPE_CMOD_REQD = 0x1f;
    private static final int ELEMENT_TYPE_CMOD_OPT = 0x20;

    private static final int TYPE_DEF = 0x0;
    private static final int TYPE_REF = 0x1;
    private static final int TYPE_SPEC = 0x2;

    //endregion

    private final TypeSig[] _requiredModifiers;
    private final TypeSig[] _optionalModifiers;
    private final TypeSig _type;

    public FieldSig(TypeSig[] requiredModifiers, TypeSig[] optionalModifiers, TypeSig type) {
        _requiredModifiers = requiredModifiers;
        _optionalModifiers = optionalModifiers;
        _type = type;
    }

    public static FieldSig parse(SignatureReader reader) {
        reader.assertUnsigned(FIELD, "FieldSig");

        List<TypeSig> requiredModifiers = new ArrayList<>();
        List<TypeSig> optionalModifiers = new ArrayList<>();
        int cmod = reader.peekUnsigned(); //custom modifier ECMA335 II. 23.2.7
        while (cmod == ELEMENT_TYPE_CMOD_REQD || cmod == ELEMENT_TYPE_CMOD_OPT) {
            reader.getUnsigned(); //read the actual cmod
            if (cmod == ELEMENT_TYPE_CMOD_REQD) {
                requiredModifiers.add(TypeSig.read(reader, null));
            } else {
                optionalModifiers.add(TypeSig.read(reader, null));
            }
            cmod = reader.peekUnsigned();
        }

        TypeSig type = TypeSig.read(reader, null);

        return new FieldSig(requiredModifiers.toArray(new TypeSig[0]), optionalModifiers.toArray(new TypeSig[0]), type);
    }

    public TypeSig[] getRequiredModifiers() {
        return _requiredModifiers;
    }

    public TypeSig[] getOptionalModifiers() {
        return _optionalModifiers;
    }

    public TypeSig getType() {
        return _type;
    }
}
