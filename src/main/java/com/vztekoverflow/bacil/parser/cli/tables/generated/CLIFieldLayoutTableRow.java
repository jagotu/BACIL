package com.vztekoverflow.bacil.parser.cli.tables.generated;

import com.vztekoverflow.bacil.parser.cli.tables.*;
public class CLIFieldLayoutTableRow extends CLITableRow<CLIFieldLayoutTableRow> {

	public CLIFieldLayoutTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	public final int getOffset() {
		int offset = 0;
		return getInt(offset);
	}

	public final CLITablePtr getField() { 
		int offset = 4;
		return new CLITablePtr(CLITableConstants.CLI_TABLE_FIELD, getShort(offset));
	}

	@Override
	public int getLength() {
		int offset = 6;
		return offset;
	}

	@Override
	public byte getTableId() {
		return CLITableConstants.CLI_TABLE_FIELD_LAYOUT;
	}

	@Override
	protected CLIFieldLayoutTableRow createNew(CLITables tables, int cursor, int rowIndex) {
		return new CLIFieldLayoutTableRow(tables, cursor, rowIndex);
	}

}
