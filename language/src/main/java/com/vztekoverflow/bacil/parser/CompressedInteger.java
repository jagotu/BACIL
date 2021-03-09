package com.vztekoverflow.bacil.parser;

public class CompressedInteger {
    public static int read(byte[] data, Positionable positionable)
    {
        int offset = positionable.getPosition();
        int result;
        if((data[offset] & 0x80) == 0)
        {
            result = data[offset] & 0x7f;
            offset += 1;
        } else if ((data[offset] & 0x40) == 0)
        {
            result = ((data[offset] & 0x3f) << 8) | (data[offset+1] & 0xFF);
            offset += 2;
        } else
        {
            result = ((data[offset] & 0x1f) << 24) | ((data[offset+1] & 0xFF) << 16) | ( (data[offset+2] & 0xFF) << 8) | (data[offset+3] & 0xFF);
            offset += 4;
        }
        positionable.setPosition(offset);
        return result;
    }
}
