package com.vztekoverflow.bacil.runtime.types;

public final class TypeHelpers {


    public static long signExtend8(long value)
    {
        return (byte)value;
    }

    public static long signExtend16(long value)
    {
        return (short)value;
    }

    public static long signExtend32(long value)
    {
        return (int)value;
    }

    public static long signExtend8to32(long value)
    {
        return truncate32(signExtend8(value));
    }

    public static long signExtend16to32(long value)
    {
        return truncate32(signExtend16(value));
    }

    public static long zeroExtend8(long value)
    {
        return value & 0xFFL;
    }


    public static long zeroExtend16(long value)
    {
        return value & 0xFFFFL;
    }

    public static long zeroExtend32(long value)
    {
        return value & 0xFFFFFFFFL;
    }

    public static long truncate8(long value)
    {
        return value & 0xFFL;
    }

    public static long truncate16(long value)
    {
        return value & 0xFFFFL;
    }

    public static long truncate32(long value)
    {
        return value & 0xFFFFFFFFL;
    }

}
