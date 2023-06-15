package com.vztekoverflow.cil.parser.cli.table.generated;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.cil.parser.cli.table.CLIStringHeapPtr;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.CLITableRow;
import com.vztekoverflow.cil.parser.cli.table.CLITables;

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

  public final CLITablePtr getResolutionScopeTablePtr() {
    int offset = 0;
    int codedValue;
    var isSmall = areSmallEnough(MAP_RESOLUTION_SCOPE_TABLES);
    if (isSmall) {
      codedValue = getShort(offset);
    } else {
      codedValue = getInt(offset);
    }
    if ((isSmall && (codedValue & 0xffff) == 0xffff)
        || (!isSmall && (codedValue & 0xffffffff) == 0xffffffff)) return null;
    return new CLITablePtr(MAP_RESOLUTION_SCOPE_TABLES[codedValue & 3], codedValue >> 2);
  }

  public final CLIStringHeapPtr getTypeNameHeapPtr() {
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

  public final CLIStringHeapPtr getTypeNamespaceHeapPtr() {
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
