package com.vztekoverflow.cil.parser.cli.table;

import com.vztekoverflow.cil.parser.bytecode.ByteUtils;
import java.util.UUID;

/** A pointer to the GUID heap, returning {@link UUID}. */
public class CLIGUIDHeapPtr extends CLIHeapPtr<UUID> {

  public CLIGUIDHeapPtr(int offset) {
    super(offset);
  }

  /**
   * Read a GUID from the GUID heap as an {@link UUID}.
   *
   * @param heapData the bytes of the heap
   * @return the GUID
   */
  @Override
  public UUID read(byte[] heapData) {
    long leastSig = ByteUtils.getLong(heapData, offset);
    long mostSig = ByteUtils.getLong(heapData, offset + 4);
    return new UUID(mostSig, leastSig);
  }
}
