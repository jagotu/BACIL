package com.vztekoverflow.cil.parser.cli.table;

import java.nio.charset.StandardCharsets;

/**
 * A pointer to the US (UserString) heap, returning a raw UTF-16 byte array. Provides a {@code
 * readString} method that returns the value as a string.
 */
public class CLIUSHeapPtr extends CLIBlobCodedHeapPtr {
  public CLIUSHeapPtr(int offset) {
    super(offset);
  }

  /**
   * Read the heap value as a string.
   *
   * @param heapData the bytes of the heap
   * @return the string value
   */
  public String readString(byte[] heapData) {
    byte[] str = read(heapData);
    return new String(str, 0, str.length - 1, StandardCharsets.UTF_16LE);
  }
}
