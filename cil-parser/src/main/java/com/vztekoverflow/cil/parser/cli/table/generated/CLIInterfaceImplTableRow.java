package com.vztekoverflow.cil.parser.cli.table.generated;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.CLITableRow;
import com.vztekoverflow.cil.parser.cli.table.CLITables;
public class CLIInterfaceImplTableRow extends CLITableRow<CLIInterfaceImplTableRow> {

	public CLIInterfaceImplTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	@CompilerDirectives.CompilationFinal(dimensions = 1)
	private static final byte[] MAP_KLASS_TABLES = new byte[] {CLITableConstants.CLI_TABLE_TYPE_DEF};
	public final CLITablePtr getKlassPtr() {
		int offset = 0;
		final int rowNo;
		if (areSmallEnough(MAP_KLASS_TABLES)) {
			rowNo = getShort(offset);
		} else {
			rowNo = getInt(offset);
		}
		return new CLITablePtr(CLITableConstants.CLI_TABLE_TYPE_DEF, rowNo);
	}

	@CompilerDirectives.CompilationFinal(dimensions = 1)
	private static final byte[] MAP_INTERFACE_TABLES = new byte[] { CLITableConstants.CLI_TABLE_TYPE_DEF, CLITableConstants.CLI_TABLE_TYPE_REF, CLITableConstants.CLI_TABLE_TYPE_SPEC} ;

	public final CLITablePtr getInterfacePtr() {
		int offset = 2;
		if (!areSmallEnough(MAP_KLASS_TABLES)) offset += 2;
		int codedValue;
		if (areSmallEnough(MAP_INTERFACE_TABLES)) {
			codedValue = getShort(offset);
		} else {
			codedValue = getInt(offset);
		}
		return new CLITablePtr(MAP_INTERFACE_TABLES[codedValue & 2], codedValue >> 2);
	}

	@Override
	public int getLength() {
		int offset = 4;
		if (!areSmallEnough(MAP_KLASS_TABLES)) offset += 2;
		if (!areSmallEnough(MAP_INTERFACE_TABLES)) offset += 2;
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
