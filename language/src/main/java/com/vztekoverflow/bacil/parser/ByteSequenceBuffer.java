package com.vztekoverflow.bacil.parser;

import org.graalvm.polyglot.io.ByteSequence;

import java.nio.charset.StandardCharsets;

/**
 * Class wrapping a ByteSequence and providing higher-level sequential read operations.
 */
public class ByteSequenceBuffer implements Positionable {

    private int position;

    /**
     * Create a new {@link ByteSequenceBuffer} wrapping the specified {@link ByteSequence}.
     * @param byteSequence the byte sequence to wrap
     */
    public ByteSequenceBuffer(ByteSequence byteSequence) {
        this.byteSequence = byteSequence;
        this.position = 0;
    }

    ByteSequence byteSequence;

    /**
     * Read a byte from the sequence.
     */
    public byte getByte()
    {
        return byteSequence.byteAt(position++);
    }

    /**
     * Read a short from the sequence.
     */
    public short getShort()
    {
        short result = (short)(getByte() & 0xff);
        result |= (getByte() & 0xff) << 8;
        return result;
    }

    /**
     * Read an int from the sequence.
     */
    public int getInt()
    {
        int result = (getByte() & 0xff);
        result |= (getByte() & 0xff) << 8;
        result |= (getByte() & 0xff) << 16;
        result |= (getByte() & 0xff) << 24;
        return result;
    }

    /**
     * Read a long from the sequence.
     */
    public long getLong()
    {
        long result = (getByte() & 0xff);
        result |= (getByte() & 0xff) << 8;
        result |= (getByte() & 0xff) << 16;
        result |= ((long)getByte() & 0xff) << 24;
        result |= ((long)getByte() & 0xff) << 32;
        result |= ((long)getByte() & 0xff) << 40;
        result |= ((long)getByte() & 0xff) << 48;
        result |= ((long)getByte() & 0xff) << 56;
        return result;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Read a null-terminated ASCII string.
     */
    public String getCString()
    {
        StringBuilder sb = new StringBuilder();

        byte curr;
        while((curr = getByte()) != 0)
        {
            sb.append((char)(curr & 0xFF));
        }

        return sb.toString();
    }


    /**
     * Read an UTF-8 string of the specified lengths.
     */
    public String getUTF8String(int length)
    {
        byte[] buf = new byte[length];
        for(int i = 0;i<length;i++)
        {
            buf[i] = getByte();
        }

        return new String(buf, StandardCharsets.UTF_8);
    }

    /**
     * Skip bytes so the position is aligned at the specified byte boundary.
     */
    public void align(int bytes)
    {
        int modulo = position % bytes;
        if(modulo != 0)
        {
            position += bytes - (position % bytes);
        }

    }

    /**
     * Get a ByteSequence starting at current position with the specified length.
     */
    public ByteSequence subSequence(int length)
    {
        return byteSequence.subSequence(position, position+length);
    }
}

