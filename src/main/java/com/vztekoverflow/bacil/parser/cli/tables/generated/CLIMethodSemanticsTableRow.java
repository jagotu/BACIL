package com.vztekoverflow.bacil.parser.cli.tables.generated;

import com.vztekoverflow.bacil.parser.cli.tables.*;
public class CLIMethodSemanticsTableRow extends CLITableRow<CLIMethodSemanticsTableRow> {

	public CLIMethodSemanticsTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	public final short getSemantics() {
		int offset = 0;
		return getShort(offset);
	}

	public final CLITablePtr getMethod() { 
		int offset = 2;
		final int rowNo;
		if (areSmallEnough(CLITableConstants.CLI_TABLE_METHOD_DEF)) {rowNo = getShort(offset);} else {rowNo = getInt(offset);}
		return new CLITablePtr(CLITableConstants.CLI_TABLE_METHOD_DEF, rowNo);
	}

	private static final byte[] MAP_ASSOCIATION_TABLES = new byte[] { CLITableConstants.CLI_TABLE_EVENT, CLITableConstants.CLI_TABLE_PROPERTY} ;
	public final CLITablePtr getAssociation() { 
		int offset = 4;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_METHOD_DEF)) offset += 2;
		int codedValue;
		if (areSmallEnough(CLITableConstants.CLI_TABLE_EVENT, CLITableConstants.CLI_TABLE_PROPERTY)) {codedValue = getShort(offset);} else {codedValue = getInt(offset);}
		return new CLITablePtr(MAP_ASSOCIATION_TABLES[codedValue & 1], codedValue >> 1);
	}

	@Override
	public int getLength() {
		int offset = 6;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_METHOD_DEF)) offset += 2;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_EVENT, CLITableConstants.CLI_TABLE_PROPERTY)) offset += 2;
		return offset;
	}

	@Override
	public byte getTableId() {
		return CLITableConstants.CLI_TABLE_METHOD_SEMANTICS;
	}

	@Override
	protected CLIMethodSemanticsTableRow createNew(CLITables tables, int cursor, int rowIndex) {
		return new CLIMethodSemanticsTableRow(tables, cursor, rowIndex);
	}

}
