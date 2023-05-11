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

    //region Constructors
    public static TypeSig  createARRAY(TypeSig type, ArrayShapeSig arrayShape) { return new TypeSig(ELEMENT_TYPE_ARRAY, 0, null, null, type, null, null, null,  arrayShape);}
    public static TypeSig  createCLASS(CLITablePtr type) {return new TypeSig(ELEMENT_TYPE_CLASS, 0, type, null, null, null, null, null,  null);}
    public static TypeSig  createFNPTR(MethodDefSig methodDefSig) {throw new CILParserException(ParserBundle.message("cli.parser.exception.not.implemented"));}
    public static TypeSig  createFNPTR(MethodRefSig methodRefSig) {throw new CILParserException(ParserBundle.message("cli.parser.exception.not.implemented"));}
    public static TypeSig  createGENERICINST(byte genericType, CLITablePtr type, TypeSig[] genParams) {return new TypeSig(genericType, 0, type, genParams, null, null, null, null,  null);}
    public static TypeSig  createMVAR(int idx) {return new TypeSig(ELEMENT_TYPE_MVAR, idx, null, null, null, null, null, null,  null);}
    public static TypeSig  createOBJECT() {return new TypeSig(ELEMENT_TYPE_OBJECT, 0, null, null, null, null, null, null,  null);}
    public static TypeSig  createPTR(CustomMod[] mods, TypeSig type) {return new TypeSig(ELEMENT_TYPE_PTR, 0, null, null, type, mods, null, null,  null);}
    public static TypeSig  createSTR() {return new TypeSig(ELEMENT_TYPE_STRING, 0, null, null, null, null, null, null,  null);}
    public static TypeSig  createSZARRAY(CustomMod[] mods, TypeSig type) {return new TypeSig(ELEMENT_TYPE_SZARRAY, 0, null, null, type, mods, null, null,  null);}
    public static TypeSig  createVALUETYPE(CLITablePtr type) {return new TypeSig(ELEMENT_TYPE_VALUETYPE, 0, type, null, null, null, null, null,  null);}
    public static TypeSig  createVAR(int idx) {return new TypeSig(ELEMENT_TYPE_VAR, idx, null, null, null, null, null, null,  null);}
    public static TypeSig createSimpleType(byte type) {return new TypeSig(type, 0, null, null, null, null, null, null,  null);}

    public TypeSig(byte _typeType, int _idx, CLITablePtr _tablePtr, TypeSig[] _genericParams, TypeSig _innerType, CustomMod[] _mods, MethodRefSig _refSig, MethodDefSig _defSig, ArrayShapeSig arrayShapeSig) {
        this._typeType = _typeType;
        this._idx = _idx;
        this._tablePtr = _tablePtr;
        this._genericParams = _genericParams;
        this._innerType = _innerType;
        this._mods = _mods;
        this._refSig = _refSig;
        this._defSig = _defSig;
        _arrayShapeSig = arrayShapeSig;
    }

    //endregion
    private final byte _typeType;
    private final int _idx;
    private final CLITablePtr _tablePtr;
    private final TypeSig[] _genericParams;
    private final TypeSig _innerType;
    private final CustomMod[] _mods;
    private final MethodRefSig _refSig;
    private final MethodDefSig _defSig;
    private final ArrayShapeSig _arrayShapeSig;

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
                return createSimpleType((byte)elementType);

            case ELEMENT_TYPE_ARRAY:
                final TypeSig type = TypeSig.read(reader, file);
                final ArrayShapeSig arrayShapeSig = null;
                return createARRAY(type, arrayShapeSig);

            case ELEMENT_TYPE_SZARRAY:
                final List<CustomMod> mods = CustomMod.readAll(reader);
                final TypeSig inner = TypeSig.read(reader, file);
                return createSZARRAY(mods != null ? (CustomMod[]) mods.toArray() : null, inner);

            case ELEMENT_TYPE_CLASS:
                return createCLASS(CLITablePtr.fromTypeDefOrRefOrSpecEncoded(reader.getUnsigned()));

            case ELEMENT_TYPE_VALUETYPE:
                return createVALUETYPE(CLITablePtr.fromTypeDefOrRefOrSpecEncoded(reader.getUnsigned()));

            case ELEMENT_TYPE_GENERICINST:
                final byte typeC = (byte)reader.getUnsigned(); // Class or Value type
                final CLITablePtr typePtr = CLITablePtr.fromTypeDefOrRefOrSpecEncoded(reader.getUnsigned());
                final int genArgCount = reader.getUnsigned();
                final TypeSig[] genParams = new TypeSig[genArgCount];
                for (int i = 0; i < genArgCount; i++) {
                    genParams[i] = TypeSig.read(reader, file);
                }
                return createGENERICINST(typeC, typePtr, genParams);

            case ELEMENT_TYPE_MVAR:
                return createMVAR(reader.getUnsigned());

            case ELEMENT_TYPE_VAR:
                return createVAR(reader.getUnsigned());

            case ELEMENT_TYPE_PTR:
                final List<CustomMod> modsC = CustomMod.readAll(reader);
                return createPTR(modsC != null ? (CustomMod[]) modsC.toArray() : null, TypeSig.read(reader, file));

            case ELEMENT_TYPE_FNPTR:
                throw new CILParserException(ParserBundle.message("cli.parser.exception.not.implemented"));

            default:
                throw new CILParserException(ParserBundle.message("cli.parser.exception.cli.type.unknown", elementType));
        }
    }
}
