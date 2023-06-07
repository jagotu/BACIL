package com.vztekoverflow.cil.parser.cli.table.generated;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.CLITableRow;
import com.vztekoverflow.cil.parser.cli.table.CLITables;
public class CLIEventMapTableRow extends CLITableRow<CLIEventMapTableRow> {

	public CLIEventMapTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	@CompilerDirectives.CompilationFinal(dimensions = 1)
	private static final byte[] MAP_PARENT_TABLES = new byte[] {CLITableConstants.CLI_TABLE_TYPE_DEF};
	public final CLITablePtr getParentTablePtr() { 
		int offset = 0;
		final int rowNo;
		if (areSmallEnough(MAP_PARENT_TABLES)) {rowNo = getShort(offset);} else {rowNo = getInt(offset);}
		return new CLITablePtr(CLITableConstants.CLI_TABLE_TYPE_DEF, rowNo);
	}

	@CompilerDirectives.CompilationFinal(dimensions = 1)
	private static final byte[] MAP_EVENT_LIST_TABLES = new byte[] {CLITableConstants.CLI_TABLE_EVENT};
	public final CLITablePtr getEventListTablePtr() { 
		int offset = 2;
		if (!areSmallEnough(MAP_PARENT_TABLES)) offset += 2;
		final int rowNo;
		if (areSmallEnough(MAP_EVENT_LIST_TABLES)) {rowNo = getShort(offset);} else {rowNo = getInt(offset);}
		return new CLITablePtr(CLITableConstants.CLI_TABLE_EVENT, rowNo);
	}

	@Override
	public int getLength() {
		int offset = 4;
		if (!areSmallEnough(MAP_PARENT_TABLES)) offset += 2;
		if (!areSmallEnough(MAP_EVENT_LIST_TABLES)) offset += 2;
		return offset;
	}

	@Override
	public byte getTableId() {
		return CLITableConstants.CLI_TABLE_EVENT_MAP;
	}

	@Override
	protected CLIEventMapTableRow createNew(CLITables tables, int cursor, int rowIndex) {
		return new CLIEventMapTableRow(tables, cursor, rowIndex);
	}

}
