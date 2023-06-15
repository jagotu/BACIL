package com.vztekoverflow.bacil.parser.cli.tables.generated;

import com.vztekoverflow.bacil.parser.cli.tables.CLIBlobHeapPtr;
import com.vztekoverflow.bacil.parser.cli.tables.CLITableRow;
import com.vztekoverflow.bacil.parser.cli.tables.CLITables;

public class CLITypeSpecTableRow extends CLITableRow<CLITypeSpecTableRow> {

  public CLITypeSpecTableRow(CLITables tables, int cursor, int rowIndex) {
    super(tables, cursor, rowIndex);
  }

  public final CLIBlobHeapPtr getSignature() {
    int offset = 0;
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
    int offset = 2;
    if (tables.isBlobHeapBig()) offset += 2;
    return offset;
  }

  @Override
  public byte getTableId() {
    return CLITableConstants.CLI_TABLE_TYPE_SPEC;
  }

  @Override
  protected CLITypeSpecTableRow createNew(CLITables tables, int cursor, int rowIndex) {
    return new CLITypeSpecTableRow(tables, cursor, rowIndex);
  }
}
