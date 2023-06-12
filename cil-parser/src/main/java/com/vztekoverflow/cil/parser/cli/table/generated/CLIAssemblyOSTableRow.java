package com.vztekoverflow.cil.parser.cli.table.generated;

import com.vztekoverflow.cil.parser.cli.table.CLITableRow;
import com.vztekoverflow.cil.parser.cli.table.CLITables;

public class CLIAssemblyOSTableRow extends CLITableRow<CLIAssemblyOSTableRow> {

  public CLIAssemblyOSTableRow(CLITables tables, int cursor, int rowIndex) {
    super(tables, cursor, rowIndex);
  }

  public final int getOSPlatformID() {
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

  @Override
  public int getLength() {
    int offset = 12;
    return offset;
  }

  @Override
  public byte getTableId() {
    return CLITableConstants.CLI_TABLE_ASSEMBLY_OS;
  }

  @Override
  protected CLIAssemblyOSTableRow createNew(CLITables tables, int cursor, int rowIndex) {
    return new CLIAssemblyOSTableRow(tables, cursor, rowIndex);
  }
}
