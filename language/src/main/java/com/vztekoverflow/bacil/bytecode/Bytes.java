package com.vztekoverflow.bacil.bytecode;

public final class Bytes {

    public static byte getByte(byte[] data, int offset)
    {
        return data[offset];
    }

    public static short getShort(byte[] data, int offset)
    {
        short result = (short)(data[offset] & 0xff);
        result |= (data[offset+1] & 0xff) << 8;
        return result;
    }

    public static int getInt(byte[] data, int offset)
    {
        int result = (data[offset] & 0xff);
        result |= (data[offset+1] & 0xff) << 8;
        result |= (data[offset+2] & 0xff) << 16;
        result |= (data[offset+3] & 0xff) << 24;
        return result;
    }

    public static long getLong(byte[] data, int offset)
    {
        long result = (data[offset] & 0xff);
        result |= (data[offset+1] & 0xff) << 8;
        result |= (data[offset+2] & 0xff) << 16;
        result |= ((long)data[offset+3] & 0xff) << 24;
        result |= ((long)data[offset+4] & 0xff) << 32;
        result |= ((long)data[offset+5] & 0xff) << 40;
        result |= ((long)data[offset+6] & 0xff) << 48;
        result |= ((long)data[offset+7] & 0xff) << 56;
        return result;
    }

    public static int getUShort(byte[] data, int offset)
    {
        int result = (data[offset] & 0xff);
        result |= (data[offset+1] & 0xff) << 8;
        return result;
    }

    public static long getUInt(byte[] data, int offset)
    {
        long result = (data[offset] & 0xff);
        result |= (data[offset+1] & 0xff) << 8;
        result |= (data[offset+2] & 0xff) << 16;
        result |= ((long)data[offset+3] & 0xff) << 24;
        return result;
    }
}
