package com.vztekoverflow.bacil.parser.signatures;

import com.vztekoverflow.bacil.parser.BACILParserException;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.runtime.types.SZArrayType;
import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.builtin.BuiltinTypes;

/**
 * Class implementing parsing for Type, as specified in II.23.2.12 Type.
 */
public class TypeSig {

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

    public static Type read(SignatureReader reader, CLIComponent component)
    {
        int elementType = reader.getUnsigned();
        BuiltinTypes builtinTypes = component.getBuiltinTypes();
        switch(elementType)
        {
            case ELEMENT_TYPE_VOID:
                return builtinTypes.getVoidType();
            case ELEMENT_TYPE_BOOLEAN:
                return builtinTypes.getBooleanType();
            case ELEMENT_TYPE_CHAR:
                return builtinTypes.getCharType();
            case ELEMENT_TYPE_U1:
                return builtinTypes.getByteType();
            case ELEMENT_TYPE_I1:
                return builtinTypes.getSbyteType();
            case ELEMENT_TYPE_U2:
                return builtinTypes.getUInt16Type();
            case ELEMENT_TYPE_I2:
                return builtinTypes.getInt16Type();
            case ELEMENT_TYPE_U4:
                return builtinTypes.getUInt32Type();
            case ELEMENT_TYPE_I4:
                return builtinTypes.getInt32Type();
            case ELEMENT_TYPE_U8:
                return builtinTypes.getUInt64Type();
            case ELEMENT_TYPE_I8:
                return builtinTypes.getInt64Type();
            case ELEMENT_TYPE_R4:
                return builtinTypes.getSingleType();
            case ELEMENT_TYPE_R8:
                return builtinTypes.getDoubleType();
            case ELEMENT_TYPE_I:
                return builtinTypes.getIntPtrType();
            case ELEMENT_TYPE_U:
                return builtinTypes.getUIntPtrType();
            case ELEMENT_TYPE_OBJECT:
                return builtinTypes.getObjectType();
            case ELEMENT_TYPE_STRING:
                return builtinTypes.getStringType();
            case ELEMENT_TYPE_TYPEDBYREF:
                return builtinTypes.getTypedReferenceType();


            case ELEMENT_TYPE_SZARRAY:
                return SZArrayType.read(reader, component);

            case ELEMENT_TYPE_CLASS:
            case ELEMENT_TYPE_VALUETYPE:
                return component.getType(CLITablePtr.fromTypeDefOrRefOrSpecEncoded(reader.getUnsigned()));



            default:
                throw new BACILParserException("Unknown type " + elementType);
        }
    }


}
