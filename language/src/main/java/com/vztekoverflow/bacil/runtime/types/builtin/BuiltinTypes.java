package com.vztekoverflow.bacil.runtime.types.builtin;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.bytecode.BytecodeInstructions;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.runtime.types.Type;

import static com.vztekoverflow.bacil.bytecode.BytecodeInstructions.*;

/**
 * Holder for the builtin types, as defined in I.8.2.2 Built-in value and reference types
 */
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

    /**
     * Create a new BuiltinTypes, resolving the types from the provided core library.
     * @param corlib core library containing implementations of the builtin types.
     */
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
        uInt64Type = corlib.findLocalType("System", "UInt64");
        intPtrType = corlib.findLocalType("System", "IntPtr");
        uIntPtrType = corlib.findLocalType("System", "UIntPtr");
        typedReferenceType = corlib.findLocalType("System", "TypedReference");
        singleType = corlib.findLocalType("System", "Single");
        doubleType = corlib.findLocalType("System", "Double");
    }


    /**
     * Get the builtin type for a typed instruction (like ldind, stind, ldelem, stelem).
     * @param opcode the instruction opcode
     * @return the builtin type for this instruction
     */
    public Type getForTypedOpcode(int opcode)
    {
        switch(opcode)
        {
            case STIND_I1:
            case LDIND_I1:
            case STELEM_I1:
            case LDELEM_I1:
                return sbyteType;

            case STIND_I2:
            case LDIND_I2:
            case STELEM_I2:
            case LDELEM_I2:

                return int16Type;

            case STIND_I4:
            case LDIND_I4:
            case STELEM_I4:
            case LDELEM_I4:
                return int32Type;

            case STIND_I8:
            case LDIND_I8:
            case STELEM_I8:
            case LDELEM_I8:
                return int64Type;

            case STIND_I:
            case LDIND_I:
            case STELEM_I:
            case LDELEM_I:
                return intPtrType;

            case STIND_R4:
            case LDIND_R4:
            case STELEM_R4:
            case LDELEM_R4:
                return singleType;

            case STIND_R8:
            case LDIND_R8:
            case STELEM_R8:
            case LDELEM_R8:
                return doubleType;

            case LDIND_U1:
            case LDELEM_U1:
                return byteType;

            case LDIND_U2:
            case LDELEM_U2:
                return uInt16Type;

            case LDIND_U4:
            case LDELEM_U4:
                return uInt32Type;

            case LDIND_REF:
            case STIND_REF:
            case LDELEM_REF:
            case STELEM_REF:
                return objectType;

            default:
                CompilerDirectives.transferToInterpreterAndInvalidate();
                throw new BACILInternalError(String.format("Can't get type for opcode %02x (%s)", opcode, BytecodeInstructions.getName(opcode)));
        }
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
