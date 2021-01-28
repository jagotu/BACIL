package com.vztekoverflow.bacil.parser.cli.tables.generated;

import com.vztekoverflow.bacil.parser.cli.tables.*;
public class CLIInterfaceImplTableRow extends CLITableRow<CLIInterfaceImplTableRow> {

	public CLIInterfaceImplTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	public final CLITablePtr getKlass() { 
		int offset = 0;
		final int rowNo;
		if (areSmallEnough(CLITableConstants.CLI_TABLE_TYPE_DEF)) {rowNo = getShort(offset);} else {rowNo = getInt(offset);}
		return new CLITablePtr(CLITableConstants.CLI_TABLE_TYPE_DEF, rowNo);
	}

	private static final byte[] MAP_INTERFACE_TABLES = new byte[] { CLITableConstants.CLI_TABLE_TYPE_DEF, CLITableConstants.CLI_TABLE_TYPE_REF, CLITableConstants.CLI_TABLE_TYPE_SPEC} ;
	public final CLITablePtr getInterface() { 
		int offset = 2;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_TYPE_DEF)) offset += 2;
		int codedValue;
		if (areSmallEnough(CLITableConstants.CLI_TABLE_TYPE_DEF, CLITableConstants.CLI_TABLE_TYPE_REF, CLITableConstants.CLI_TABLE_TYPE_SPEC)) {codedValue = getShort(offset);} else {codedValue = getInt(offset);}
		return new CLITablePtr(MAP_INTERFACE_TABLES[codedValue & 3], codedValue >> 2);
	}

	@Override
	public int getLength() {
		int offset = 4;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_TYPE_DEF)) offset += 2;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_TYPE_DEF, CLITableConstants.CLI_TABLE_TYPE_REF, CLITableConstants.CLI_TABLE_TYPE_SPEC)) offset += 2;
		return offset;
	}

	@Override
	public byte getTableId() {
		return CLITableConstants.CLI_TABLE_INTERFACE_IMPL;
	}

	@Override
	protected CLIInterfaceImplTableRow createNew(CLITables tables, int cursor, int rowIndex) {
		return new CLIInterfaceImplTableRow(tables, cursor, rowIndex);
	}

}
