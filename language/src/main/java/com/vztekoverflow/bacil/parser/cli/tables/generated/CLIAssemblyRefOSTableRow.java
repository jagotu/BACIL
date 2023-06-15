package com.vztekoverflow.bacil.parser.cli.tables.generated;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.parser.cli.tables.CLITableRow;
import com.vztekoverflow.bacil.parser.cli.tables.CLITables;

public class CLIAssemblyRefOSTableRow extends CLITableRow<CLIAssemblyRefOSTableRow> {

  @CompilerDirectives.CompilationFinal(dimensions = 1)
  private static final byte[] MAP_ASSEMBLY_REF_TABLES =
      new byte[] {CLITableConstants.CLI_TABLE_ASSEMBLY_REF};

  public CLIAssemblyRefOSTableRow(CLITables tables, int cursor, int rowIndex) {
    super(tables, cursor, rowIndex);
  }

  public final int getOSPlatformId() {
    int offset = 0;
    return getInt(offset);
  }

  public final int getOSMajorVersion() {
    int offset = 4;
    return getInt(offset);
  }

  public final int getOSMinorVersion() {
    int offset = 8;
    return getInt(offset);
  }

  public final CLITablePtr getAssemblyRef() {
    int offset = 12;
    final int rowNo;
    if (areSmallEnough(MAP_ASSEMBLY_REF_TABLES)) {
      rowNo = getShort(offset);
    } else {
      rowNo = getInt(offset);
    }
    return new CLITablePtr(CLITableConstants.CLI_TABLE_ASSEMBLY_REF, rowNo);
  }

  @Override
  public int getLength() {
    int offset = 14;
    if (!areSmallEnough(MAP_ASSEMBLY_REF_TABLES)) offset += 2;
    return offset;
  }

  @Override
  public byte getTableId() {
    return CLITableConstants.CLI_TABLE_ASSEMBLY_REF_OS;
  }

  @Override
  protected CLIAssemblyRefOSTableRow createNew(CLITables tables, int cursor, int rowIndex) {
    return new CLIAssemblyRefOSTableRow(tables, cursor, rowIndex);
  }
}
