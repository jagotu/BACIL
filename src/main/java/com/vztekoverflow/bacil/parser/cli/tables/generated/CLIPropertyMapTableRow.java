package com.vztekoverflow.bacil.parser.cli.tables.generated;

import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.parser.cli.tables.CLITableRow;
import com.vztekoverflow.bacil.parser.cli.tables.CLITables;
public class CLIPropertyMapTableRow extends CLITableRow<CLIPropertyMapTableRow> {

	public CLIPropertyMapTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	public final CLITablePtr getParent() { 
		int offset = 0;
		final int rowNo;
		if (areSmallEnough(CLITableConstants.CLI_TABLE_TYPE_DEF)) {rowNo = getShort(offset);} else {rowNo = getInt(offset);}
		return new CLITablePtr(CLITableConstants.CLI_TABLE_TYPE_DEF, rowNo);
	}

	public final CLITablePtr getPropertyList() { 
		int offset = 2;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_TYPE_DEF)) offset += 2;
		final int rowNo;
		if (areSmallEnough(CLITableConstants.CLI_TABLE_PROPERTY)) {rowNo = getShort(offset);} else {rowNo = getInt(offset);}
		return new CLITablePtr(CLITableConstants.CLI_TABLE_PROPERTY, rowNo);
	}

	@Override
	public int getLength() {
		int offset = 4;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_TYPE_DEF)) offset += 2;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_PROPERTY)) offset += 2;
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
