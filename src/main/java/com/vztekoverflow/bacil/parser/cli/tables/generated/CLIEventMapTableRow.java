package com.vztekoverflow.bacil.parser.cli.tables.generated;

import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.parser.cli.tables.CLITableRow;
import com.vztekoverflow.bacil.parser.cli.tables.CLITables;
public class CLIEventMapTableRow extends CLITableRow<CLIEventMapTableRow> {

	public CLIEventMapTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	public final CLITablePtr getParent() { 
		int offset = 0;
		final int rowNo;
		if (areSmallEnough(CLITableConstants.CLI_TABLE_TYPE_DEF)) {rowNo = getShort(offset);} else {rowNo = getInt(offset);}
		return new CLITablePtr(CLITableConstants.CLI_TABLE_TYPE_DEF, rowNo);
	}

	public final CLITablePtr getEventList() { 
		int offset = 2;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_TYPE_DEF)) offset += 2;
		final int rowNo;
		if (areSmallEnough(CLITableConstants.CLI_TABLE_EVENT)) {rowNo = getShort(offset);} else {rowNo = getInt(offset);}
		return new CLITablePtr(CLITableConstants.CLI_TABLE_EVENT, rowNo);
	}

	@Override
	public int getLength() {
		int offset = 4;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_TYPE_DEF)) offset += 2;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_EVENT)) offset += 2;
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
