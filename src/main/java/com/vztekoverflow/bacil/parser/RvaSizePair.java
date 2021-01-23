package com.vztekoverflow.bacil.parser;

public class RvaSizePair {
    private final int rva;
    private final int size;

    public RvaSizePair(int rva, int size) {
        this.rva = rva;
        this.size = size;
    }

    public int getRva() {
        return rva;
    }

    public int getSize() {
        return size;
    }

    public static RvaSizePair read(ByteSequenceBuffer buf)
    {
        return new RvaSizePair(buf.getInt(), buf.getInt());
    }
}
