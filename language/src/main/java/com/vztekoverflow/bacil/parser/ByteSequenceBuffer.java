package com.vztekoverflow.bacil.parser;

import org.graalvm.polyglot.io.ByteSequence;

import java.nio.charset.StandardCharsets;

public class ByteSequenceBuffer {

    private int position;

    public ByteSequenceBuffer(ByteSequence byteSequence) {
        this.byteSequence = byteSequence;
        this.position = 0;
    }

    ByteSequence byteSequence;

    public byte getByte()
    {
        return byteSequence.byteAt(position++);
    }

    public short getShort()
    {
        short result = (short)(getByte() & 0xff);
        result |= (getByte() & 0xff) << 8;
        return result;
    }

    public int getInt()
    {
        int result = (getByte() & 0xff);
        result |= (getByte() & 0xff) << 8;
        result |= (getByte() & 0xff) << 16;
        result |= (getByte() & 0xff) << 24;
        return result;
    }

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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

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

    public String getUTF8String(int length)
    {
        byte[] buf = new byte[length];
        for(int i = 0;i<length;i++)
        {
            buf[i] = getByte();
        }

        return new String(buf, StandardCharsets.UTF_8);
    }

    public void align(int bytes)
    {
        int modulo = position % bytes;
        if(modulo != 0)
        {
            position += bytes - (position % bytes);
        }

    }

    public ByteSequence subSequence(int length)
    {
        return byteSequence.subSequence(position, position+length);
    }
}

