package com.vztekoverflow.bacil.runtime.types;

import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.parser.signatures.SignatureReader;
import com.vztekoverflow.bacil.parser.signatures.SignatureType;

import java.util.ArrayList;
import java.util.List;

public class CustomMod {

    private final boolean optional;
    private final CLITablePtr typeToken;

    public CustomMod(boolean optional, CLITablePtr typeToken) {
        this.optional = optional;
        this.typeToken = typeToken;
    }

    public boolean isOptional() {
        return optional;
    }

    public CLITablePtr getTypeToken() {
        return typeToken;
    }

    public static boolean canStart(SignatureReader reader)
    {
        return reader.peekUnsigned() == SignatureType.ELEMENT_TYPE_CMOD_OPT || reader.peekUnsigned() == SignatureType.ELEMENT_TYPE_CMOD_REQD;
    }

    public static List<CustomMod> readAll(SignatureReader reader)
    {
        List<CustomMod> mods = null;
        if (CustomMod.canStart(reader)) {
            mods = new ArrayList<>();
            while (CustomMod.canStart(reader)) {
                mods.add(CustomMod.read(reader));
            }
        }
        return mods;
    }

    public static CustomMod read(SignatureReader reader)
    {
        int cmod = reader.getUnsigned();
        final boolean optional;
        optional = cmod == SignatureType.ELEMENT_TYPE_CMOD_OPT;

        CLITablePtr typeToken = CLITablePtr.fromTypeDefOrRefOrSpecEncoded(reader.getUnsigned());

        return new CustomMod(optional, typeToken);
    }
}
