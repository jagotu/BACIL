package com.vztekoverflow.cil.parser.cli.table.generated;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.cil.parser.cli.table.CLIBlobHeapPtr;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.CLITableRow;
import com.vztekoverflow.cil.parser.cli.table.CLITables;

public class CLIFieldMarshalTableRow extends CLITableRow<CLIFieldMarshalTableRow> {

  @CompilerDirectives.CompilationFinal(dimensions = 1)
  private static final byte[] MAP_PARENT_TABLES =
      new byte[] {CLITableConstants.CLI_TABLE_FIELD, CLITableConstants.CLI_TABLE_PARAM};

  public CLIFieldMarshalTableRow(CLITables tables, int cursor, int rowIndex) {
    super(tables, cursor, rowIndex);
  }

  public final CLITablePtr getParentTablePtr() {
    int offset = 0;
    int codedValue;
    var isSmall = areSmallEnough(MAP_PARENT_TABLES);
    if (isSmall) {
      codedValue = getShort(offset);
    } else {
      codedValue = getInt(offset);
    }
    if ((isSmall && (codedValue & 0xffff) == 0xffff)
        || (!isSmall && (codedValue & 0xffffffff) == 0xffffffff)) return null;
    return new CLITablePtr(MAP_PARENT_TABLES[codedValue & 1], codedValue >> 1);
  }

  public final CLIBlobHeapPtr getNativeTypeHeapPtr() {
    int offset = 2;
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
    int offset = 4;
    if (tables.isBlobHeapBig()) offset += 2;
    if (!areSmallEnough(MAP_PARENT_TABLES)) offset += 2;
    return offset;
  }

  @Override
  public byte getTableId() {
    return CLITableConstants.CLI_TABLE_FIELD_MARSHAL;
  }

  @Override
  protected CLIFieldMarshalTableRow createNew(CLITables tables, int cursor, int rowIndex) {
    return new CLIFieldMarshalTableRow(tables, cursor, rowIndex);
  }
}
