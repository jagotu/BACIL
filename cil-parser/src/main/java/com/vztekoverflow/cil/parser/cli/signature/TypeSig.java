package com.vztekoverflow.cil.parser.cli.signature;

import com.vztekoverflow.cil.parser.CILParserException;
import com.vztekoverflow.cil.parser.ParserBundle;
import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;

import java.util.List;

public class TypeSig {
    //region Constants
    public static final byte ELEMENT_TYPE_END = 0x00;
    public static final byte ELEMENT_TYPE_VOID = 0x01;
    public static final byte ELEMENT_TYPE_BOOLEAN = 0x02;
    public static final byte ELEMENT_TYPE_CHAR = 0x03;
    public static final byte ELEMENT_TYPE_I1 = 0x04;
    public static final byte ELEMENT_TYPE_U1 = 0x05;
    public static final byte ELEMENT_TYPE_I2 = 0x06;
    public static final byte ELEMENT_TYPE_U2 = 0x07;
    public static final byte ELEMENT_TYPE_I4 = 0x08;
    public static final byte ELEMENT_TYPE_U4 = 0x09;
    public static final byte ELEMENT_TYPE_I8 = 0x0a;
    public static final byte ELEMENT_TYPE_U8 = 0x0b;
    public static final byte ELEMENT_TYPE_R4 = 0x0c;
    public static final byte ELEMENT_TYPE_R8 = 0x0d;
    public static final byte ELEMENT_TYPE_STRING = 0x0e;
    public static final byte ELEMENT_TYPE_PTR = 0x0f;
    public static final byte ELEMENT_TYPE_BYREF = 0x10;
    public static final byte ELEMENT_TYPE_VALUETYPE = 0x11;
    public static final byte ELEMENT_TYPE_CLASS = 0x12;
    public static final byte ELEMENT_TYPE_VAR = 0x13;
    public static final byte ELEMENT_TYPE_ARRAY = 0x14;
    public static final byte ELEMENT_TYPE_GENERICINST = 0x15;
    public static final byte ELEMENT_TYPE_TYPEDBYREF = 0x16;
    public static final byte ELEMENT_TYPE_I = 0x18;
    public static final byte ELEMENT_TYPE_U = 0x19;
    public static final byte ELEMENT_TYPE_FNPTR = 0x1b;
    public static final byte ELEMENT_TYPE_OBJECT = 0x1c;
    public static final byte ELEMENT_TYPE_SZARRAY = 0x1d;
    public static final byte ELEMENT_TYPE_MVAR = 0x1e;
    public static final byte ELEMENT_TYPE_CMOD_REQD = 0x1f;
    public static final byte ELEMENT_TYPE_CMOD_OPT = 0x20;
    public static final byte ELEMENT_TYPE_INTERNAL = 0x21;
    public static final byte ELEMENT_TYPE_PINNED = 0x45;
    //endregion

    public TypeSig(CLITablePtr _type, TypeSig _inner, TypeSig[] genericParams, CustomMod[] _mods, int _typeByte, int idx) {
        this._type = _type;
        this._inner = _inner;
        _genericParams = genericParams;
        this._mods = _mods;
        this._typeByte = _typeByte;
        _idx = idx;
    }

    private final CLITablePtr _type;
    private final TypeSig _inner;
    private final TypeSig[] _genericParams;
    private final CustomMod[] _mods;
    private final int _typeByte;
    private final int _idx;

    public static TypeSig read(SignatureReader reader, CLIFile file) {
        int elementType = reader.getUnsigned();
        switch (elementType) {
            case ELEMENT_TYPE_VOID:
            case ELEMENT_TYPE_BOOLEAN:
            case ELEMENT_TYPE_CHAR:
            case ELEMENT_TYPE_U1:
            case ELEMENT_TYPE_I1:
            case ELEMENT_TYPE_U2:
            case ELEMENT_TYPE_I2:
            case ELEMENT_TYPE_U4:
            case ELEMENT_TYPE_I4:
            case ELEMENT_TYPE_U8:
            case ELEMENT_TYPE_I8:
            case ELEMENT_TYPE_R4:
            case ELEMENT_TYPE_R8:
            case ELEMENT_TYPE_I:
            case ELEMENT_TYPE_U:
            case ELEMENT_TYPE_OBJECT:
            case ELEMENT_TYPE_STRING:
            case ELEMENT_TYPE_TYPEDBYREF:
                return new TypeSig(null, null, null, null, elementType, 0);

            case ELEMENT_TYPE_SZARRAY:
                final List<CustomMod> mods = CustomMod.readAll(reader);
                final TypeSig inner = TypeSig.read(reader, file);
                return new TypeSig(null, inner, null, mods != null ? (CustomMod[]) mods.toArray() : null, elementType, 0);

            case ELEMENT_TYPE_CLASS:
            case ELEMENT_TYPE_VALUETYPE:
                return new TypeSig(CLITablePtr.fromTypeDefOrRefOrSpecEncoded(reader.getUnsigned()), null, null, null, elementType, 0);
            case ELEMENT_TYPE_GENERICINST:
                reader.getUnsigned(); //Class or Value type
                final CLITablePtr type = CLITablePtr.fromTypeDefOrRefOrSpecEncoded(reader.getUnsigned());
                final int genArgCount = reader.getUnsigned();
                final TypeSig[] genParams = new TypeSig[genArgCount];
                for (int i = 0; i < genArgCount; i++) {
                    genParams[i] = TypeSig.read(reader, file);
                }
                return new TypeSig(type, null, genParams, null, elementType, 0);
            case ELEMENT_TYPE_MVAR:
            case ELEMENT_TYPE_VAR:
                final int idx = reader.getUnsigned();
                return new TypeSig(null, null, null, null, elementType, idx);
            default:
                throw new CILParserException(ParserBundle.message("cli.parser.exception.cli.type.unknown", elementType));
        }
    }
}
