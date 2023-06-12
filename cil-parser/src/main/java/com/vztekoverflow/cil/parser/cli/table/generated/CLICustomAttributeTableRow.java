package com.vztekoverflow.cil.parser.cli.table.generated;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.cil.parser.cli.table.CLIBlobHeapPtr;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.CLITableRow;
import com.vztekoverflow.cil.parser.cli.table.CLITables;

public class CLICustomAttributeTableRow extends CLITableRow<CLICustomAttributeTableRow> {

  @CompilerDirectives.CompilationFinal(dimensions = 1)
  private static final byte[] MAP_PARENT_TABLES =
      new byte[] {
        CLITableConstants.CLI_TABLE_METHOD_DEF,
        CLITableConstants.CLI_TABLE_FIELD,
        CLITableConstants.CLI_TABLE_TYPE_REF,
        CLITableConstants.CLI_TABLE_TYPE_DEF,
        CLITableConstants.CLI_TABLE_PARAM,
        CLITableConstants.CLI_TABLE_INTERFACE_IMPL,
        CLITableConstants.CLI_TABLE_MEMBER_REF,
        CLITableConstants.CLI_TABLE_MODULE,
        CLITableConstants.CLI_TABLE_DECL_SECURITY,
        CLITableConstants.CLI_TABLE_PROPERTY,
        CLITableConstants.CLI_TABLE_EVENT,
        CLITableConstants.CLI_TABLE_STAND_ALONE_SIG,
        CLITableConstants.CLI_TABLE_MODULE_REF,
        CLITableConstants.CLI_TABLE_TYPE_SPEC,
        CLITableConstants.CLI_TABLE_ASSEMBLY,
        CLITableConstants.CLI_TABLE_ASSEMBLY_REF,
        CLITableConstants.CLI_TABLE_FILE,
        CLITableConstants.CLI_TABLE_EXPORTED_TYPE,
        CLITableConstants.CLI_TABLE_MANIFEST_RESOURCE,
        CLITableConstants.CLI_TABLE_GENERIC_PARAM,
        CLITableConstants.CLI_TABLE_GENERIC_PARAM_CONSTRAINT,
        CLITableConstants.CLI_TABLE_METHOD_SPEC
      };
  @CompilerDirectives.CompilationFinal(dimensions = 1)
  private static final byte[] MAP_TYPE_TABLES =
      new byte[] {
        CLITableConstants.CLI_TABLE_NONE_ID,
        CLITableConstants.CLI_TABLE_NONE_ID,
        CLITableConstants.CLI_TABLE_METHOD_DEF,
        CLITableConstants.CLI_TABLE_MEMBER_REF
      };

  public CLICustomAttributeTableRow(CLITables tables, int cursor, int rowIndex) {
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
    return new CLITablePtr(MAP_PARENT_TABLES[codedValue & 31], codedValue >> 5);
  }

  public final CLITablePtr getTypeTablePtr() {
    int offset = 2;
    if (!areSmallEnough(MAP_PARENT_TABLES)) offset += 2;
    int codedValue;
    var isSmall = areSmallEnough(MAP_TYPE_TABLES);
    if (isSmall) {
      codedValue = getShort(offset);
    } else {
      codedValue = getInt(offset);
    }
    if ((isSmall && (codedValue & 0xffff) == 0xffff)
        || (!isSmall && (codedValue & 0xffffffff) == 0xffffffff)) return null;
    return new CLITablePtr(MAP_TYPE_TABLES[codedValue & 3], codedValue >> 2);
  }

  public final CLIBlobHeapPtr getValueHeapPtr() {
    int offset = 4;
    if (!areSmallEnough(MAP_PARENT_TABLES)) offset += 2;
    if (!areSmallEnough(MAP_TYPE_TABLES)) offset += 2;
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
    if (!areSmallEnough(MAP_TYPE_TABLES)) offset += 2;
    return offset;
  }

  @Override
  public byte getTableId() {
    return CLITableConstants.CLI_TABLE_CUSTOM_ATTRIBUTE;
  }

  @Override
  protected CLICustomAttributeTableRow createNew(CLITables tables, int cursor, int rowIndex) {
    return new CLICustomAttributeTableRow(tables, cursor, rowIndex);
  }
}
