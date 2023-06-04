package com.vztekoverflow.cil.parser.cli.table.generated;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.cil.parser.cli.table.CLIStringHeapPtr;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.CLITableRow;
import com.vztekoverflow.cil.parser.cli.table.CLITables;
public class CLIGenericParamTableRow extends CLITableRow<CLIGenericParamTableRow> {

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

	@CompilerDirectives.CompilationFinal(dimensions = 1)
	private static final byte[] MAP_OWNER_TABLES = new byte[] { CLITableConstants.CLI_TABLE_TYPE_DEF, CLITableConstants.CLI_TABLE_METHOD_DEF} ;
	public final CLITablePtr getOwnerTablePtr() {
        int offset = 4;
        int codedValue;
        var isSmall = areSmallEnough(MAP_OWNER_TABLES);
        if (isSmall) {
            codedValue = getShort(offset);
        } else {
            codedValue = getInt(offset);
        }
        if ((isSmall && (codedValue & 0xffff) == 0xffff) || (!isSmall && (codedValue & 0xffffffff) == 0xffffffff))
            return null;
        return new CLITablePtr(MAP_OWNER_TABLES[codedValue & 1], codedValue >> 1);
    }

	public final CLIStringHeapPtr getNameHeapPtr() {
		int offset = 6;
		if (!areSmallEnough(MAP_OWNER_TABLES)) offset += 2;
		int heapOffset=0;
		if (tables.isStringHeapBig()) { heapOffset = getInt(offset); } else { heapOffset = getUShort(offset); }
		return new CLIStringHeapPtr(heapOffset);
	}

	@Override
	public int getLength() {
		int offset = 8;
		if (tables.isStringHeapBig()) offset += 2;
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
