package com.vztekoverflow.cil.parser.cli.table.generated;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.cil.parser.cli.table.*;
public class CLIFieldLayoutTableRow extends CLITableRow<CLIFieldLayoutTableRow> {

	public CLIFieldLayoutTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	public final int getOffset() {
		int offset = 0;
		return getInt(offset);
	}

	@CompilerDirectives.CompilationFinal(dimensions = 1)
	private static final byte[] MAP_FIELD_TABLES = new byte[] {CLITableConstants.CLI_TABLE_FIELD};
	public final CLITablePtr getField() { 
		int offset = 4;
		final int rowNo;
		if (areSmallEnough(MAP_FIELD_TABLES)) {rowNo = getShort(offset);} else {rowNo = getInt(offset);}
		return new CLITablePtr(CLITableConstants.CLI_TABLE_FIELD, rowNo);
	}

	@Override
	public int getLength() {
		int offset = 6;
		if (!areSmallEnough(MAP_FIELD_TABLES)) offset += 2;
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
