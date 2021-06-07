package com.vztekoverflow.bacil.parser.cli.tables.generated;

import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.parser.cli.tables.CLITableRow;
import com.vztekoverflow.bacil.parser.cli.tables.CLITables;
public class CLIFieldRVATableRow extends CLITableRow<CLIFieldRVATableRow> {

	public CLIFieldRVATableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	public final int getRVA() {
		int offset = 0;
		return getInt(offset);
	}

	public final CLITablePtr getField() { 
		int offset = 4;
		final int rowNo;
		if (areSmallEnough(CLITableConstants.CLI_TABLE_FIELD)) {rowNo = getShort(offset);} else {rowNo = getInt(offset);}
		return new CLITablePtr(CLITableConstants.CLI_TABLE_FIELD, rowNo);
	}

	@Override
	public int getLength() {
		int offset = 6;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_FIELD)) offset += 2;
		return offset;
	}

	@Override
	public byte getTableId() {
		return CLITableConstants.CLI_TABLE_FIELD_RVA;
	}

	@Override
	protected CLIFieldRVATableRow createNew(CLITables tables, int cursor, int rowIndex) {
		return new CLIFieldRVATableRow(tables, cursor, rowIndex);
	}

}