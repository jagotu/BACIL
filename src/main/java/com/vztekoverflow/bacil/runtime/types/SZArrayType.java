package com.vztekoverflow.bacil.runtime.types;

import com.vztekoverflow.bacil.parser.signatures.SignatureReader;

import java.util.List;

public class SZArrayType extends Type {

    public static final String TYPE = "SZArrayType";

    private final Type inner;

    public SZArrayType(Type inner) {
        this.inner = inner;
    }

    @Override
    public byte getTypeCategory() {
        return Type.ELEMENT_TYPE_SZARRAY;
    }

    public static SZArrayType read(SignatureReader reader)
    {
        reader.assertUnsigned(Type.ELEMENT_TYPE_SZARRAY, TYPE);
        List<CustomMod> mods = CustomMod.readAll(reader);

        Type inner = Type.create(reader);

        return new SZArrayType(inner);

    }
}
