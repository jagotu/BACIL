package com.vztekoverflow.bacil.parser.cli.tables.generated;

import com.vztekoverflow.bacil.parser.cli.tables.*;
public class CLIPropertyMapTableRow extends CLITableRow<CLIPropertyMapTableRow> {

	public CLIPropertyMapTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	public final CLITablePtr getParent() { 
		int offset = 0;
		return new CLITablePtr(CLITableConstants.CLI_TABLE_TYPE_DEF, getShort(offset));
	}

	public final CLITablePtr getPropertyList() { 
		int offset = 2;
		return new CLITablePtr(CLITableConstants.CLI_TABLE_PROPERTY, getShort(offset));
	}

	@Override
	public int getLength() {
		int offset = 4;
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
