package com.vztekoverflow.bacil.parser.cli.tables;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.parser.ByteSequenceBuffer;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITableConstants;

/**
 * Class representing information stored in the CLI Metadata tables stream (#~) header, as described
 * in II.24.2.6 #~ stream.
 */
public class CLITablesHeader {

  private final byte majorVersion;
  private final byte minorVersion;
  private final byte heapSizes;
  private final long valid;
  private final long sorted;

  @CompilerDirectives.CompilationFinal(dimensions = 1)
  private final int[] rowCounts;

  public CLITablesHeader(
      byte majorVersion,
      byte minorVersion,
      byte heapSizes,
      long valid,
      long sorted,
      int[] rowCounts) {
    this.majorVersion = majorVersion;
    this.minorVersion = minorVersion;
    this.heapSizes = heapSizes;
    this.valid = valid;
    this.sorted = sorted;
    this.rowCounts = rowCounts;
  }

  /**
   * Read the CLI Metadata tables header from the provided {@link ByteSequenceBuffer}
   *
   * @param buf the byte sequence to read the CLI Metadata tables header
   * @return the CLI tables header represented as a {@link CLITablesHeader} instance
   */
  public static CLITablesHeader read(ByteSequenceBuffer buf) {
    buf.getInt(); // Reserved
    final byte majorVersion = buf.getByte();
    final byte minorVersion = buf.getByte();
    final byte heapSizes = buf.getByte();
    buf.getByte(); // Reserved
    final long valid = buf.getLong();
    final long sorted = buf.getLong();

    final int[] rowCounts = new int[CLITableConstants.CLI_TABLE_MAX_ID + 1];

    for (int i = 0; i <= CLITableConstants.CLI_TABLE_MAX_ID; i++) {
      if ((valid & (1L << i)) != 0) {
        rowCounts[i] = buf.getInt();
      }
    }

    return new CLITablesHeader(majorVersion, minorVersion, heapSizes, valid, sorted, rowCounts);
  }

  public byte getMajorVersion() {
    return majorVersion;
  }

  public byte getMinorVersion() {
    return minorVersion;
  }

  public byte getHeapSizes() {
    return heapSizes;
  }

  public long getValid() {
    return valid;
  }

  public long getSorted() {
    return sorted;
  }

  public final int getRowCount(byte table) {
    return rowCounts[table];
  }
}
