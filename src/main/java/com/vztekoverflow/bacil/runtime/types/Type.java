package com.vztekoverflow.bacil.runtime.types;

import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.parser.BACILParserException;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLIMethodDefTableRow;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITypeDefTableRow;
import com.vztekoverflow.bacil.parser.signatures.SignatureReader;

import java.text.ParseException;
import java.util.List;

public abstract class Type {

    public abstract byte getTypeCategory();

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

    public static Type create(SignatureReader reader)
    {
        int elementType = reader.peekUnsigned();
        switch(elementType)
        {
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
                reader.getUnsigned();
                return new SimpleType((byte)elementType);


            case ELEMENT_TYPE_SZARRAY:
                return SZArrayType.read(reader);


            default:
                throw new BACILParserException("Unknown type " + elementType);
        }
    }

    public static Type fromTypeDef(CLIComponent component, CLITypeDefTableRow row)
    {
        if(row == null)
            return null;

        if(row.getTypeNamespace().read(component.getStringHeap()).equals("System"))
        {
            //II.23.2.16
            switch (row.getTypeName().read(component.getStringHeap())) {
                case "String":
                    return new SimpleType(Type.ELEMENT_TYPE_STRING);
                case "Object":
                    return new SimpleType(Type.ELEMENT_TYPE_OBJECT);
                case "Void":
                    return new SimpleType(Type.ELEMENT_TYPE_VOID);
                case "Boolean":
                    return new SimpleType(Type.ELEMENT_TYPE_BOOLEAN);
                case "Char":
                    return new SimpleType(Type.ELEMENT_TYPE_CHAR);
                case "Byte":
                    return new SimpleType(Type.ELEMENT_TYPE_U1);
                case "Sbyte":
                    return new SimpleType(Type.ELEMENT_TYPE_I1);
                case "Int16":
                    return new SimpleType(Type.ELEMENT_TYPE_I2);
                case "UInt16":
                    return new SimpleType(Type.ELEMENT_TYPE_U2);
                case "Int32":
                    return new SimpleType(Type.ELEMENT_TYPE_I4);
                case "UInt32":
                    return new SimpleType(Type.ELEMENT_TYPE_U4);
                case "Int64":
                    return new SimpleType(Type.ELEMENT_TYPE_I8);
                case "UInt64":
                    return new SimpleType(Type.ELEMENT_TYPE_U8);
                case "IntPtr":
                    return new SimpleType(Type.ELEMENT_TYPE_I);
                case "UIntPtr":
                    return new SimpleType(Type.ELEMENT_TYPE_U);
                case "TypedReference":
                    return new SimpleType(Type.ELEMENT_TYPE_TYPEDBYREF);


            }
        }


        return new KlassType();
    }

    public static Type thisWrap(Type type)
    {
        switch(type.getTypeCategory())
        {
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
                return new ByRefWrapped(type);
        }
        return type;
    }

    public static Type readParam(SignatureReader reader, boolean allowVoid)
    {
        boolean byRef = false;
        List<CustomMod> mods = CustomMod.readAll(reader);

        if (reader.peekUnsigned() == Type.ELEMENT_TYPE_TYPEDBYREF) {
            reader.getUnsigned();
            return SimpleType.TYPEDBYREF;
        }

        if (allowVoid && reader.peekUnsigned() == Type.ELEMENT_TYPE_VOID) {
            reader.getUnsigned();
            return SimpleType.VOID;
        }

        if (reader.peekUnsigned() == Type.ELEMENT_TYPE_BYREF)
        {
            byRef = true;
            reader.getUnsigned();
        }

        Type type = create(reader);
        if(byRef)
        {
            type = new ByRefWrapped(type);
        }

        if(mods != null)
        {
            type = new CustomModWrapped(type, mods);
        }

        return type;
    }

    public Object defaultConstructor()
    {
        return null;
    }

    public Object fromStackVar(Object ref, long primitive)
    {
        throw new BACILInternalError("Conversion from stack var to type " + getTypeCategory() + " not implemented!");
    }

    public void toStackVar(Object[] refs, long[] primitives, int slot, Object value)
    {
        refs[slot] = value;
    }
}
