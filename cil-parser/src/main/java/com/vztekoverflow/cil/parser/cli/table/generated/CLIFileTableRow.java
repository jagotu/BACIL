package com.vztekoverflow.cil.parser.cli.table.generated;

import com.vztekoverflow.cil.parser.cli.table.CLIBlobHeapPtr;
import com.vztekoverflow.cil.parser.cli.table.CLIStringHeapPtr;
import com.vztekoverflow.cil.parser.cli.table.CLITableRow;
import com.vztekoverflow.cil.parser.cli.table.CLITables;

public class CLIFileTableRow extends CLITableRow<CLIFileTableRow> {

  public CLIFileTableRow(CLITables tables, int cursor, int rowIndex) {
    super(tables, cursor, rowIndex);
  }

  public final int getFlags() {
    int offset = 0;
    return getInt(offset);
  }

  public final CLIStringHeapPtr getNameHeapPtr() {
    int offset = 4;
    int heapOffset = 0;
    if (tables.isStringHeapBig()) {
      heapOffset = getInt(offset);
    } else {
      heapOffset = getUShort(offset);
    }
    return new CLIStringHeapPtr(heapOffset);
  }

  public final CLIBlobHeapPtr getHashValueHeapPtr() {
    int offset = 6;
    if (tables.isStringHeapBig()) offset += 2;
    int heapOffset = 0;
    if (tables.isBlobHeapBig()) {
      heapOffset = getInt(offset);
    } else {
      heapOffset = getUShort(offset);
    }
    return new CLIBlobHeapPtr(heapOffset);
  }

  @Override
  public int getLength() {
    int offset = 8;
    if (tables.isStringHeapBig()) offset += 2;
    if (tables.isBlobHeapBig()) offset += 2;
    return offset;
  }

  @Override
  public byte getTableId() {
    return CLITableConstants.CLI_TABLE_FILE;
  }

  @Override
  protected CLIFileTableRow createNew(CLITables tables, int cursor, int rowIndex) {
    return new CLIFileTableRow(tables, cursor, rowIndex);
  }
}
