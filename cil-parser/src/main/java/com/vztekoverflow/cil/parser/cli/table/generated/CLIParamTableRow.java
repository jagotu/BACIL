package com.vztekoverflow.cil.parser.cli.table.generated;

import com.vztekoverflow.cil.parser.cli.table.CLIStringHeapPtr;
import com.vztekoverflow.cil.parser.cli.table.CLITableRow;
import com.vztekoverflow.cil.parser.cli.table.CLITables;

public class CLIParamTableRow extends CLITableRow<CLIParamTableRow> {

  public CLIParamTableRow(CLITables tables, int cursor, int rowIndex) {
    super(tables, cursor, rowIndex);
  }

  public final short getFlags() {
    int offset = 0;
    return getShort(offset);
  }

  public final short getSequence() {
    int offset = 2;
    return getShort(offset);
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

  @Override
  public int getLength() {
    int offset = 6;
    if (tables.isStringHeapBig()) offset += 2;
    return offset;
  }

  @Override
  public byte getTableId() {
    return CLITableConstants.CLI_TABLE_PARAM;
  }

  @Override
  protected CLIParamTableRow createNew(CLITables tables, int cursor, int rowIndex) {
    return new CLIParamTableRow(tables, cursor, rowIndex);
  }
}
