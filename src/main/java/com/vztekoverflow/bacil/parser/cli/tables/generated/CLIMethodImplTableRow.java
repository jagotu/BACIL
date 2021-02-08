package com.vztekoverflow.bacil.parser.cli.tables.generated;

import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.parser.cli.tables.CLITableRow;
import com.vztekoverflow.bacil.parser.cli.tables.CLITables;
public class CLIMethodImplTableRow extends CLITableRow<CLIMethodImplTableRow> {

	public CLIMethodImplTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	public final CLITablePtr getKlass() { 
		int offset = 0;
		final int rowNo;
		if (areSmallEnough(CLITableConstants.CLI_TABLE_TYPE_DEF)) {rowNo = getShort(offset);} else {rowNo = getInt(offset);}
		return new CLITablePtr(CLITableConstants.CLI_TABLE_TYPE_DEF, rowNo);
	}

	private static final byte[] MAP_METHOD_BODY_TABLES = new byte[] { CLITableConstants.CLI_TABLE_METHOD_DEF, CLITableConstants.CLI_TABLE_MEMBER_REF} ;
	public final CLITablePtr getMethodBody() { 
		int offset = 2;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_TYPE_DEF)) offset += 2;
		int codedValue;
		if (areSmallEnough(CLITableConstants.CLI_TABLE_METHOD_DEF, CLITableConstants.CLI_TABLE_MEMBER_REF)) {codedValue = getShort(offset);} else {codedValue = getInt(offset);}
		return new CLITablePtr(MAP_METHOD_BODY_TABLES[codedValue & 1], codedValue >> 1);
	}

	private static final byte[] MAP_METHOD_DECLARATION_TABLES = new byte[] { CLITableConstants.CLI_TABLE_METHOD_DEF, CLITableConstants.CLI_TABLE_MEMBER_REF} ;
	public final CLITablePtr getMethodDeclaration() { 
		int offset = 4;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_TYPE_DEF)) offset += 2;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_METHOD_DEF, CLITableConstants.CLI_TABLE_MEMBER_REF)) offset += 2;
		int codedValue;
		if (areSmallEnough(CLITableConstants.CLI_TABLE_METHOD_DEF, CLITableConstants.CLI_TABLE_MEMBER_REF)) {codedValue = getShort(offset);} else {codedValue = getInt(offset);}
		return new CLITablePtr(MAP_METHOD_DECLARATION_TABLES[codedValue & 1], codedValue >> 1);
	}

	@Override
	public int getLength() {
		int offset = 6;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_TYPE_DEF)) offset += 2;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_METHOD_DEF, CLITableConstants.CLI_TABLE_MEMBER_REF)) offset += 2;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_METHOD_DEF, CLITableConstants.CLI_TABLE_MEMBER_REF)) offset += 2;
		return offset;
	}

	@Override
	public byte getTableId() {
		return CLITableConstants.CLI_TABLE_METHOD_IMPL;
	}

	@Override
	protected CLIMethodImplTableRow createNew(CLITables tables, int cursor, int rowIndex) {
		return new CLIMethodImplTableRow(tables, cursor, rowIndex);
	}

}
