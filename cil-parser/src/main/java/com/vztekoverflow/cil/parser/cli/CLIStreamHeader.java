package com.vztekoverflow.cil.parser.cli;

import com.vztekoverflow.cil.parser.ByteSequenceBuffer;

/** Representation of a CLI stream header as specified in II.24.2.2 Stream header. */
public class CLIStreamHeader {
  private final int offset;
  private final int size;
  private final String name;

  public CLIStreamHeader(int offset, int size, String name) {
    this.offset = offset;
    this.size = size;
    this.name = name;
  }

  /**
   * Read a CLI stream header from the provided {@link ByteSequenceBuffer}.
   *
   * @param buf the byte sequence to read the CLI stream header from
   * @return the CLI stream header represented as a {@link CLIStreamHeader} instance
   */
  public static CLIStreamHeader read(ByteSequenceBuffer buf) {
    final int offset = buf.getInt();
    final int size = buf.getInt();
    final String name = buf.getCString();
    buf.align(4);

    return new CLIStreamHeader(offset, size, name);
  }

  public int getOffset() {
    return offset;
  }

  public int getSize() {
    return size;
  }

  public String getName() {
    return name;
  }
}
