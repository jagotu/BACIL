package com.vztekoverflow.cil.parser.cli.table.generated;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.cil.parser.cli.table.*;

public class CLIMemberRefTableRow extends CLITableRow<CLIMemberRefTableRow> {

  @CompilerDirectives.CompilationFinal(dimensions = 1)
  private static final byte[] MAP_KLASS_TABLES =
      new byte[] {
        CLITableConstants.CLI_TABLE_TYPE_DEF,
        CLITableConstants.CLI_TABLE_TYPE_REF,
        CLITableConstants.CLI_TABLE_MODULE_REF,
        CLITableConstants.CLI_TABLE_METHOD_DEF,
        CLITableConstants.CLI_TABLE_TYPE_SPEC
      };

  public CLIMemberRefTableRow(CLITables tables, int cursor, int rowIndex) {
    super(tables, cursor, rowIndex);
  }

  public final CLITablePtr getKlassTablePtr() {
    int offset = 0;
    int codedValue;
    var isSmall = areSmallEnough(MAP_KLASS_TABLES);
    if (isSmall) {
      codedValue = getShort(offset);
    } else {
      codedValue = getInt(offset);
    }
    if ((isSmall && (codedValue & 0xffff) == 0xffff)
        || (!isSmall && (codedValue & 0xffffffff) == 0xffffffff)) return null;
    return new CLITablePtr(MAP_KLASS_TABLES[codedValue & 7], codedValue >> 3);
  }

  public final CLIStringHeapPtr getNameHeapPtr() {
    int offset = 2;
    if (!areSmallEnough(MAP_KLASS_TABLES)) offset += 2;
    int heapOffset = 0;
    if (tables.isStringHeapBig()) {
      heapOffset = getInt(offset);
    } else {
      heapOffset = getUShort(offset);
    }
    return new CLIStringHeapPtr(heapOffset);
  }

  public final CLIBlobHeapPtr getSignatureHeapPtr() {
    int offset = 4;
    if (tables.isStringHeapBig()) offset += 2;
    if (!areSmallEnough(MAP_KLASS_TABLES)) offset += 2;
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
    if (tables.isStringHeapBig()) offset += 2;
    if (tables.isBlobHeapBig()) offset += 2;
    if (!areSmallEnough(MAP_KLASS_TABLES)) offset += 2;
    return offset;
  }

  @Override
  public byte getTableId() {
    return CLITableConstants.CLI_TABLE_MEMBER_REF;
  }

  @Override
  protected CLIMemberRefTableRow createNew(CLITables tables, int cursor, int rowIndex) {
    return new CLIMemberRefTableRow(tables, cursor, rowIndex);
  }
}
