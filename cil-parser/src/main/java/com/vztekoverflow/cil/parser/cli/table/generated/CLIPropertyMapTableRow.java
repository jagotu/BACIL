package com.vztekoverflow.cil.parser.cli.table.generated;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.cil.parser.cli.table.*;
public class CLIPropertyMapTableRow extends CLITableRow<CLIPropertyMapTableRow> {

	public CLIPropertyMapTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	@CompilerDirectives.CompilationFinal(dimensions = 1)
	private static final byte[] MAP_PARENT_TABLES = new byte[] {CLITableConstants.CLI_TABLE_TYPE_DEF};
	public final CLITablePtr getParent() { 
		int offset = 0;
		final int rowNo;
		if (areSmallEnough(MAP_PARENT_TABLES)) {rowNo = getShort(offset);} else {rowNo = getInt(offset);}
		return new CLITablePtr(CLITableConstants.CLI_TABLE_TYPE_DEF, rowNo);
	}

	@CompilerDirectives.CompilationFinal(dimensions = 1)
	private static final byte[] MAP_PROPERTY_LIST_TABLES = new byte[] {CLITableConstants.CLI_TABLE_PROPERTY};
	public final CLITablePtr getPropertyList() { 
		int offset = 2;
		if (!areSmallEnough(MAP_PARENT_TABLES)) offset += 2;
		final int rowNo;
		if (areSmallEnough(MAP_PROPERTY_LIST_TABLES)) {rowNo = getShort(offset);} else {rowNo = getInt(offset);}
		return new CLITablePtr(CLITableConstants.CLI_TABLE_PROPERTY, rowNo);
	}

	@Override
	public int getLength() {
		int offset = 4;
		if (!areSmallEnough(MAP_PARENT_TABLES)) offset += 2;
		if (!areSmallEnough(MAP_PROPERTY_LIST_TABLES)) offset += 2;
		return offset;
	}

	@Override
	public byte getTableId() {
		return CLITableConstants.CLI_TABLE_PROPERTY_MAP;
	}

	@Override
	protected CLIPropertyMapTableRow createNew(CLITables tables, int cursor, int rowIndex) {
		return new CLIPropertyMapTableRow(tables, cursor, rowIndex);
	}

}
