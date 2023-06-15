package com.vztekoverflow.cil.parser.cli.table.generated;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.cil.parser.cli.table.CLIStringHeapPtr;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.CLITableRow;
import com.vztekoverflow.cil.parser.cli.table.CLITables;

public class CLIEventTableRow extends CLITableRow<CLIEventTableRow> {

  @CompilerDirectives.CompilationFinal(dimensions = 1)
  private static final byte[] MAP_EVENT_TYPE_TABLES =
      new byte[] {
        CLITableConstants.CLI_TABLE_TYPE_DEF,
        CLITableConstants.CLI_TABLE_TYPE_REF,
        CLITableConstants.CLI_TABLE_TYPE_SPEC
      };

  public CLIEventTableRow(CLITables tables, int cursor, int rowIndex) {
    super(tables, cursor, rowIndex);
  }

  public final short getEventFlags() {
    int offset = 0;
    return getShort(offset);
  }

  public final CLIStringHeapPtr getNameHeapPtr() {
    int offset = 2;
    int heapOffset = 0;
    if (tables.isStringHeapBig()) {
      heapOffset = getInt(offset);
    } else {
      heapOffset = getUShort(offset);
    }
    return new CLIStringHeapPtr(heapOffset);
  }

  public final CLITablePtr getEventTypeTablePtr() {
    int offset = 4;
    if (tables.isStringHeapBig()) offset += 2;
    int codedValue;
    var isSmall = areSmallEnough(MAP_EVENT_TYPE_TABLES);
    if (isSmall) {
      codedValue = getShort(offset);
    } else {
      codedValue = getInt(offset);
    }
    if ((isSmall && (codedValue & 0xffff) == 0xffff)
        || (!isSmall && (codedValue & 0xffffffff) == 0xffffffff)) return null;
    return new CLITablePtr(MAP_EVENT_TYPE_TABLES[codedValue & 3], codedValue >> 2);
  }

  @Override
  public int getLength() {
    int offset = 6;
    if (tables.isStringHeapBig()) offset += 2;
    if (!areSmallEnough(MAP_EVENT_TYPE_TABLES)) offset += 2;
    return offset;
  }

  @Override
  public byte getTableId() {
    return CLITableConstants.CLI_TABLE_EVENT;
  }

  @Override
  protected CLIEventTableRow createNew(CLITables tables, int cursor, int rowIndex) {
    return new CLIEventTableRow(tables, cursor, rowIndex);
  }
}
