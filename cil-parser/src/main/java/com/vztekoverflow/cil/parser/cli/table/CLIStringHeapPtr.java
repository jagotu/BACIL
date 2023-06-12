package com.vztekoverflow.cil.parser.cli.table;

import java.nio.charset.StandardCharsets;

/** A pointer to the String heap, returning a {@link String}. */
public class CLIStringHeapPtr extends CLIHeapPtr<String> {

  public CLIStringHeapPtr(int offset) {
    super(offset);
  }

  /**
   * Read a string from the string heap.
   *
   * @param heapData the bytes of the heap
   * @return the string value
   */
  @Override
  public String read(byte[] heapData) {
    // The strings are null-terminated UTF-8 strings.
    int nullByteOffset;

    for (nullByteOffset = offset; nullByteOffset < heapData.length; nullByteOffset++) {
      if (heapData[nullByteOffset] == 0) break;
    }

    return new String(heapData, offset, nullByteOffset - offset, StandardCharsets.UTF_8);
  }
}
