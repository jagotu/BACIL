package com.vztekoverflow.bacil.parser.cli.tables.generated;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.parser.cli.tables.CLIBlobHeapPtr;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.parser.cli.tables.CLITableRow;
import com.vztekoverflow.bacil.parser.cli.tables.CLITables;

public class CLIConstantTableRow extends CLITableRow<CLIConstantTableRow> {

  @CompilerDirectives.CompilationFinal(dimensions = 1)
  private static final byte[] MAP_PARENT_TABLES =
      new byte[] {
        CLITableConstants.CLI_TABLE_FIELD,
        CLITableConstants.CLI_TABLE_PARAM,
        CLITableConstants.CLI_TABLE_PROPERTY
      };

  public CLIConstantTableRow(CLITables tables, int cursor, int rowIndex) {
    super(tables, cursor, rowIndex);
  }

  public final byte getType() {
    int offset = 0;
    return getByte(offset);
  }

  public final CLITablePtr getParent() {
    int offset = 2;
    int codedValue;
    if (areSmallEnough(MAP_PARENT_TABLES)) {
      codedValue = getShort(offset);
    } else {
      codedValue = getInt(offset);
    }
    return new CLITablePtr(MAP_PARENT_TABLES[codedValue & 3], codedValue >> 2);
  }

  public final CLIBlobHeapPtr getValue() {
    int offset = 4;
    if (!areSmallEnough(MAP_PARENT_TABLES)) offset += 2;
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
    int offset = 6;
    if (tables.isBlobHeapBig()) offset += 2;
    if (!areSmallEnough(MAP_PARENT_TABLES)) offset += 2;
    return offset;
  }

  @Override
  public byte getTableId() {
    return CLITableConstants.CLI_TABLE_CONSTANT;
  }

  @Override
  protected CLIConstantTableRow createNew(CLITables tables, int cursor, int rowIndex) {
    return new CLIConstantTableRow(tables, cursor, rowIndex);
  }
}
