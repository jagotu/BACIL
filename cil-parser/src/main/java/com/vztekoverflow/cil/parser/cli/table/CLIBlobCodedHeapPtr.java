package com.vztekoverflow.cil.parser.cli.table;

import com.vztekoverflow.cil.parser.CompressedInteger;
import com.vztekoverflow.cil.parser.Positionable;
import java.util.Arrays;

/**
 * Represents a pointer to a "blob-coded" heap, whose blobs are length prefixed as described in
 * II.24.2.4 #US and #Blob heaps.
 */
public abstract class CLIBlobCodedHeapPtr extends CLIHeapPtr<byte[]> implements Positionable {

  private int position = offset;

  public CLIBlobCodedHeapPtr(int offset) {
    super(offset);
  }

  /** Get the current position in the heap data. */
  @Override
  public int getPosition() {
    return position;
  }

  /** Set the current position in the heap data. */
  @Override
  public void setPosition(int position) {
    this.position = position;
  }

  /**
   * Get the blob data as a byte[].
   *
   * @param heapData the bytes of the heap
   * @return the data of the blob
   */
  @Override
  public byte[] read(byte[] heapData) {
    int length = CompressedInteger.read(heapData, this);

    return Arrays.copyOfRange(heapData, position, position + length);
  }
}
