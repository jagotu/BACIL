package com.vztekoverflow.cil.parser;

/**
 * Class implementing the integer decompression algorithm described in II.23.2 Blobs and signatures
 */
public class CompressedInteger {

    /**
     * Read a compressed integer from the specified byte array, setting the positionable at the end of it.
     * @param data the raw bytes
     * @param positionable positionable that's set before the compressed integer
     * @return the value of the compressed integer
     */
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
