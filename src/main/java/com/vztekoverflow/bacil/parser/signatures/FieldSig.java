package com.vztekoverflow.bacil.parser.signatures;

import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.runtime.types.CustomMod;
import com.vztekoverflow.bacil.runtime.types.Type;

import java.util.List;

//II.23.2.4
public class FieldSig {

    private static final String TYPE = "FieldSig";
    private final Type type;
    private final List<CustomMod> mods;

    public FieldSig(Type type, List<CustomMod> mods) {
        this.type = type;
        this.mods = mods;
    }

    public static FieldSig read(byte[] signature, CLIComponent component) {
        SignatureReader reader = new SignatureReader(signature);
        reader.assertUnsigned(6, TYPE);

        List<CustomMod> mods = CustomMod.readAll(reader);
        Type type = SignatureType.read(reader, component);

        return new FieldSig(type, mods);

    }

    public Type getType() {
        return type;
    }

    public List<CustomMod> getMods() {
        return mods;
    }
}
