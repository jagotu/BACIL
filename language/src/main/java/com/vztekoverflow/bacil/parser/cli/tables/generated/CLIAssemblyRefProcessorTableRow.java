package com.vztekoverflow.bacil.parser.cli.tables.generated;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.parser.cli.tables.CLITableRow;
import com.vztekoverflow.bacil.parser.cli.tables.CLITables;

public class CLIAssemblyRefProcessorTableRow extends CLITableRow<CLIAssemblyRefProcessorTableRow> {

  @CompilerDirectives.CompilationFinal(dimensions = 1)
  private static final byte[] MAP_ASSEMBLY_REF_TABLES =
      new byte[] {CLITableConstants.CLI_TABLE_ASSEMBLY_REF};

  public CLIAssemblyRefProcessorTableRow(CLITables tables, int cursor, int rowIndex) {
    super(tables, cursor, rowIndex);
  }

  public final int getProcessor() {
    int offset = 0;
    return getInt(offset);
  }

  public final CLITablePtr getAssemblyRef() {
    int offset = 4;
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
    int offset = 6;
    if (!areSmallEnough(MAP_ASSEMBLY_REF_TABLES)) offset += 2;
    return offset;
  }

  @Override
  public byte getTableId() {
    return CLITableConstants.CLI_TABLE_ASSEMBLY_REF_PROCESSOR;
  }

  @Override
  protected CLIAssemblyRefProcessorTableRow createNew(CLITables tables, int cursor, int rowIndex) {
    return new CLIAssemblyRefProcessorTableRow(tables, cursor, rowIndex);
  }
}
