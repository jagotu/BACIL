package com.vztekoverflow.bacil.runtime.types;

import com.vztekoverflow.bacil.parser.cil.CILMethod;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.signatures.MethodDefSig;
import com.vztekoverflow.bacil.parser.signatures.SignatureReader;
import com.vztekoverflow.bacil.parser.signatures.TypeSig;

import java.util.List;

public class SZArrayType extends Type {

    public static final String TYPE = "SZArrayType";

    public Type getInner() {
        return inner;
    }

    private final Type inner;

    public SZArrayType(Type inner) {
        this.inner = inner;
    }

    public static SZArrayType read(SignatureReader reader, CLIComponent component)
    {
        List<CustomMod> mods = CustomMod.readAll(reader);

        Type inner = TypeSig.read(reader, component);

        return new SZArrayType(inner);
    }


    @Override
    public Type getDirectBaseClass() {
        //TODO return system.array
        return null;
    }

    @Override
    public CILMethod getMemberMethod(String name, MethodDefSig signature) {
        return null;
    }

    @Override
    public boolean isByRef() {
        return false;
    }

    @Override
    public boolean isPinned() {
        return false;
    }

    @Override
    public List<CustomMod> getMods() {
        return null;
    }
}
