package com.vztekoverflow.bacil.parser;

/** Class representing a pair of an RVA and a size, used in PE and CLI headers. */
public class RvaSizePair {
  private final int rva;
  private final int size;

  public RvaSizePair(int rva, int size) {
    this.rva = rva;
    this.size = size;
  }

  public static RvaSizePair read(ByteSequenceBuffer buf) {
    return new RvaSizePair(buf.getInt(), buf.getInt());
  }

  public int getRva() {
    return rva;
  }

  public int getSize() {
    return size;
  }
}
