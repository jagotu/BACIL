package com.vztekoverflow.bacil.parser.cli.tables.generated;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.parser.cli.tables.CLITableRow;
import com.vztekoverflow.bacil.parser.cli.tables.CLITables;

public class CLIGenericParamTableRow extends CLITableRow<CLIGenericParamTableRow> {

  @CompilerDirectives.CompilationFinal(dimensions = 1)
  private static final byte[] MAP_OWNER_TABLES =
      new byte[] {
        CLITableConstants.CLI_TABLE_TYPE_DEF,
        CLITableConstants.CLI_TABLE_TYPE_REF,
        CLITableConstants.CLI_TABLE_TYPE_SPEC
      };

  public CLIGenericParamTableRow(CLITables tables, int cursor, int rowIndex) {
    super(tables, cursor, rowIndex);
  }

  public final short getNumber() {
    int offset = 0;
    return getShort(offset);
  }

  public final short getFlags() {
    int offset = 2;
    return getShort(offset);
  }

  public final CLITablePtr getOwner() {
    int offset = 4;
    int codedValue;
    if (areSmallEnough(MAP_OWNER_TABLES)) {
      codedValue = getShort(offset);
    } else {
      codedValue = getInt(offset);
    }
    return new CLITablePtr(MAP_OWNER_TABLES[codedValue & 3], codedValue >> 2);
  }

  @Override
  public int getLength() {
    int offset = 6;
    if (!areSmallEnough(MAP_OWNER_TABLES)) offset += 2;
    return offset;
  }

  @Override
  public byte getTableId() {
    return CLITableConstants.CLI_TABLE_GENERIC_PARAM;
  }

  @Override
  protected CLIGenericParamTableRow createNew(CLITables tables, int cursor, int rowIndex) {
    return new CLIGenericParamTableRow(tables, cursor, rowIndex);
  }
}
