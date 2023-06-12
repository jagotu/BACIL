package com.vztekoverflow.bacil.parser.cli.tables.generated;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.parser.cli.tables.CLIStringHeapPtr;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.parser.cli.tables.CLITableRow;
import com.vztekoverflow.bacil.parser.cli.tables.CLITables;

public class CLITypeRefTableRow extends CLITableRow<CLITypeRefTableRow> {

  @CompilerDirectives.CompilationFinal(dimensions = 1)
  private static final byte[] MAP_RESOLUTION_SCOPE_TABLES =
      new byte[] {
        CLITableConstants.CLI_TABLE_MODULE,
        CLITableConstants.CLI_TABLE_MODULE_REF,
        CLITableConstants.CLI_TABLE_ASSEMBLY_REF,
        CLITableConstants.CLI_TABLE_TYPE_REF
      };

  public CLITypeRefTableRow(CLITables tables, int cursor, int rowIndex) {
    super(tables, cursor, rowIndex);
  }

  public final CLITablePtr getResolutionScope() {
    int offset = 0;
    int codedValue;
    if (areSmallEnough(MAP_RESOLUTION_SCOPE_TABLES)) {
      codedValue = getShort(offset);
    } else {
      codedValue = getInt(offset);
    }
    return new CLITablePtr(MAP_RESOLUTION_SCOPE_TABLES[codedValue & 3], codedValue >> 2);
  }

  public final CLIStringHeapPtr getTypeName() {
    int offset = 2;
    if (!areSmallEnough(MAP_RESOLUTION_SCOPE_TABLES)) offset += 2;
    int heapOffset = 0;
    if (tables.isStringHeapBig()) {
      heapOffset = getInt(offset);
    } else {
      heapOffset = getUShort(offset);
    }
    return new CLIStringHeapPtr(heapOffset);
  }

  public final CLIStringHeapPtr getTypeNamespace() {
    int offset = 4;
    if (tables.isStringHeapBig()) offset += 2;
    if (!areSmallEnough(MAP_RESOLUTION_SCOPE_TABLES)) offset += 2;
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
    if (tables.isStringHeapBig()) offset += 4;
    if (!areSmallEnough(MAP_RESOLUTION_SCOPE_TABLES)) offset += 2;
    return offset;
  }

  @Override
  public byte getTableId() {
    return CLITableConstants.CLI_TABLE_TYPE_REF;
  }

  @Override
  protected CLITypeRefTableRow createNew(CLITables tables, int cursor, int rowIndex) {
    return new CLITypeRefTableRow(tables, cursor, rowIndex);
  }
}
