package com.vztekoverflow.bacil.parser.cli.tables.generated;

import com.vztekoverflow.bacil.parser.cli.tables.*;
public class CLIEventMapTableRow extends CLITableRow<CLIEventMapTableRow> {

	public CLIEventMapTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	public final CLITablePtr getParent() { 
		int offset = 0;
		return new CLITablePtr(CLITableConstants.CLI_TABLE_TYPE_DEF, getShort(offset));
	}

	public final CLITablePtr getEventList() { 
		int offset = 2;
		return new CLITablePtr(CLITableConstants.CLI_TABLE_EVENT, getShort(offset));
	}

	@Override
	public int getLength() {
		int offset = 4;
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
