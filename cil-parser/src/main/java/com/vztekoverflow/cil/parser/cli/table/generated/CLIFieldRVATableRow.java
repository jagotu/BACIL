package com.vztekoverflow.cil.parser.cli.table.generated;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.cil.parser.cli.table.*;
public class CLIFieldRVATableRow extends CLITableRow<CLIFieldRVATableRow> {

	public CLIFieldRVATableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	public final int getRVA() {
		int offset = 0;
		return getInt(offset);
	}

	@CompilerDirectives.CompilationFinal(dimensions = 1)
	private static final byte[] MAP_FIELD_TABLES = new byte[] {CLITableConstants.CLI_TABLE_FIELD};
	public final CLITablePtr getFieldTablePtr() { 
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
		return CLITableConstants.CLI_TABLE_FIELD_RVA;
	}

	@Override
	protected CLIFieldRVATableRow createNew(CLITables tables, int cursor, int rowIndex) {
		return new CLIFieldRVATableRow(tables, cursor, rowIndex);
	}

}
