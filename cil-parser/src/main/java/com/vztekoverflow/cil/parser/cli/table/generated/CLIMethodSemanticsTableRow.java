package com.vztekoverflow.cil.parser.cli.table.generated;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.CLITableRow;
import com.vztekoverflow.cil.parser.cli.table.CLITables;

public class CLIMethodSemanticsTableRow extends CLITableRow<CLIMethodSemanticsTableRow> {

  @CompilerDirectives.CompilationFinal(dimensions = 1)
  private static final byte[] MAP_METHOD_TABLES =
      new byte[] {CLITableConstants.CLI_TABLE_METHOD_DEF};
  @CompilerDirectives.CompilationFinal(dimensions = 1)
  private static final byte[] MAP_ASSOCIATION_TABLES =
      new byte[] {CLITableConstants.CLI_TABLE_EVENT, CLITableConstants.CLI_TABLE_PROPERTY};

  public CLIMethodSemanticsTableRow(CLITables tables, int cursor, int rowIndex) {
    super(tables, cursor, rowIndex);
  }

  public final short getSemantics() {
    int offset = 0;
    return getShort(offset);
  }

  public final CLITablePtr getMethodTablePtr() {
    int offset = 2;
    final int rowNo;
    if (areSmallEnough(MAP_METHOD_TABLES)) {
      rowNo = getShort(offset);
    } else {
      rowNo = getInt(offset);
    }
    return new CLITablePtr(CLITableConstants.CLI_TABLE_METHOD_DEF, rowNo);
  }

  public final CLITablePtr getAssociationTablePtr() {
    int offset = 4;
    if (!areSmallEnough(MAP_METHOD_TABLES)) offset += 2;
    int codedValue;
    var isSmall = areSmallEnough(MAP_ASSOCIATION_TABLES);
    if (isSmall) {
      codedValue = getShort(offset);
    } else {
      codedValue = getInt(offset);
    }
    if ((isSmall && (codedValue & 0xffff) == 0xffff)
        || (!isSmall && (codedValue & 0xffffffff) == 0xffffffff)) return null;
    return new CLITablePtr(MAP_ASSOCIATION_TABLES[codedValue & 1], codedValue >> 1);
  }

  @Override
  public int getLength() {
    int offset = 6;
    if (!areSmallEnough(MAP_METHOD_TABLES)) offset += 2;
    if (!areSmallEnough(MAP_ASSOCIATION_TABLES)) offset += 2;
    return offset;
  }

  @Override
  public byte getTableId() {
    return CLITableConstants.CLI_TABLE_METHOD_SEMANTICS;
  }

  @Override
  protected CLIMethodSemanticsTableRow createNew(CLITables tables, int cursor, int rowIndex) {
    return new CLIMethodSemanticsTableRow(tables, cursor, rowIndex);
  }
}
