package com.vztekoverflow.bacil.runtime.types.builtin;

import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.runtime.types.Type;

public class BuiltinTypes {
    private final Type stringType;
    private final Type objectType;
    private final Type voidType;
    private final Type booleanType;
    private final Type charType;
    private final Type byteType;
    private final Type sbyteType;
    private final Type int16Type;
    private final Type uInt16Type;
    private final Type int32Type;
    private final Type uInt32Type;
    private final Type int64Type;
    private final Type uInt64Type;
    private final Type intPtrType;
    private final Type uIntPtrType;
    private final Type typedReferenceType;
    private final Type valueTypeType;
    private final Type singleType;
    private final Type doubleType;

    public BuiltinTypes(CLIComponent corlib) {
        objectType = corlib.findLocalType("System", "Object");
        valueTypeType = corlib.findLocalType("System", "ValueType");
        stringType = corlib.findLocalType("System", "String");
        voidType = corlib.findLocalType("System", "Void");
        booleanType = corlib.findLocalType("System", "Boolean");
        charType = corlib.findLocalType("System", "Char");
        byteType = corlib.findLocalType("System", "Byte");
        sbyteType = corlib.findLocalType("System", "SByte");
        int16Type = corlib.findLocalType("System", "Int16");
        uInt16Type = corlib.findLocalType("System", "UInt16");
        int32Type = corlib.findLocalType("System", "Int32");
        uInt32Type = corlib.findLocalType("System", "UInt32");
        int64Type = corlib.findLocalType("System", "Int64");
        uInt64Type = corlib.findLocalType("System", "UInt16");
        intPtrType = corlib.findLocalType("System", "IntPtr");
        uIntPtrType = corlib.findLocalType("System", "UIntPtr");
        typedReferenceType = corlib.findLocalType("System", "TypedReference");
        singleType = corlib.findLocalType("System", "Single");
        doubleType = corlib.findLocalType("System", "Double");
    }

    public Type getStringType() {
        return stringType;
    }

    public Type getObjectType() {
        return objectType;
    }

    public Type getVoidType() {
        return voidType;
    }

    public Type getBooleanType() {
        return booleanType;
    }

    public Type getCharType() {
        return charType;
    }

    public Type getByteType() {
        return byteType;
    }

    public Type getSbyteType() {
        return sbyteType;
    }

    public Type getInt16Type() {
        return int16Type;
    }

    public Type getUInt16Type() {
        return uInt16Type;
    }

    public Type getInt32Type() {
        return int32Type;
    }

    public Type getUInt32Type() {
        return uInt32Type;
    }

    public Type getInt64Type() {
        return int64Type;
    }

    public Type getUInt64Type() {
        return uInt64Type;
    }

    public Type getIntPtrType() {
        return intPtrType;
    }

    public Type getUIntPtrType() {
        return uIntPtrType;
    }

    public Type getTypedReferenceType() {
        return typedReferenceType;
    }

    public Type getValueTypeType() {
        return valueTypeType;
    }

    public Type getSingleType() {
        return singleType;
    }

    public Type getDoubleType() {
        return doubleType;
    }
}
