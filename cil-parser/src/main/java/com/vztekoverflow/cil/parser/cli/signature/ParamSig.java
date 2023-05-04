package com.vztekoverflow.cil.parser.cli.signature;

import com.vztekoverflow.cil.parser.cli.CLIFile;

import java.lang.reflect.Type;
import java.util.List;

public class ParamSig {
    private final boolean _byRef;
    private final CustomMod[] _mods;

    private final boolean _void;
    private final boolean _typedByRef;
    private final TypeSig _typeSig;

    public ParamSig(boolean _byRef, CustomMod[] _mods, boolean _void, boolean _typedByRef, TypeSig _typeSig) {
        this._byRef = _byRef;
        this._mods = _mods;
        this._void = _void;
        this._typedByRef = _typedByRef;
        this._typeSig = _typeSig;
    }

    public static ParamSig parse(SignatureReader reader, CLIFile file, boolean allowVoid) {
        boolean byRef = false;
        List<CustomMod> mods = CustomMod.readAll(reader);

        if (reader.peekUnsigned() == TypeSig.ELEMENT_TYPE_TYPEDBYREF) {
            reader.getUnsigned();
            return new ParamSig(false, null, false, true, null);
        }

        if (allowVoid && reader.peekUnsigned() == TypeSig.ELEMENT_TYPE_VOID) {
            reader.getUnsigned();
            return new ParamSig(false, null, true, false, null);
        }

        if (reader.peekUnsigned() == TypeSig.ELEMENT_TYPE_BYREF)
        {
            byRef = true;
            reader.getUnsigned();
        }

        TypeSig type = TypeSig.read(reader, file);

        return new ParamSig(byRef, mods != null ? (CustomMod[]) mods.toArray() : null, false, false, type);
    }

    public boolean isByRef() {return _byRef;}
    public boolean isVoid() {return _void;}
    public boolean isTypedByRef() {return _typedByRef;}
    public TypeSig getTypeSig() {return _typeSig;}
}
