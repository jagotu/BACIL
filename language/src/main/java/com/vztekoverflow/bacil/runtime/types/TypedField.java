package com.vztekoverflow.bacil.runtime.types;

import com.vztekoverflow.bacil.parser.signatures.FieldSig;

import java.util.List;

/**
 * Represents a field in a type.
 */
public class TypedField {

    private final FieldSig signature;
    private final short flags;
    private final String name;
    private final int offset;

    public static final short FIELD_ATTRIBUTE_STATIC = 0x0010;

    public TypedField(short flags, String name, FieldSig signature, int offset) {
        this.flags = flags;
        this.name = name;
        this.signature = signature;
        this.offset = offset;
    }

    public FieldSig getSignature() {
        return signature;
    }

    public short getFlags() {
        return flags;
    }

    public String getName() {
        return name;
    }

    public Type getType()
    {
        return signature.getType();
    }

    public List<CustomMod> getMods()
    {
        return signature.getMods();
    }

    public int getOffset() {
        return offset;
    }

    public boolean isStatic() {
        return (flags & TypedField.FIELD_ATTRIBUTE_STATIC) != 0;
    }
}
