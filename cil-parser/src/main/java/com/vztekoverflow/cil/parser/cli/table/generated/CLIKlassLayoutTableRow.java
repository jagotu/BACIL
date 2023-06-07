package com.vztekoverflow.cil.parser.cli.table.generated;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.CLITableRow;
import com.vztekoverflow.cil.parser.cli.table.CLITables;
public class CLIKlassLayoutTableRow extends CLITableRow<CLIKlassLayoutTableRow> {

	public CLIKlassLayoutTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	public final short getPackingSize() {
		int offset = 0;
		return getShort(offset);
	}

	public final int getKlassSize() {
		int offset = 2;
		return getInt(offset);
	}

	@CompilerDirectives.CompilationFinal(dimensions = 1)
	private static final byte[] MAP_PARENT_TABLES = new byte[] {CLITableConstants.CLI_TABLE_TYPE_DEF};
	public final CLITablePtr getParentTablePtr() { 
		int offset = 6;
		final int rowNo;
		if (areSmallEnough(MAP_PARENT_TABLES)) {rowNo = getShort(offset);} else {rowNo = getInt(offset);}
		return new CLITablePtr(CLITableConstants.CLI_TABLE_TYPE_DEF, rowNo);
	}

	@Override
	public int getLength() {
		int offset = 8;
		if (!areSmallEnough(MAP_PARENT_TABLES)) offset += 2;
		return offset;
	}

	@Override
	public byte getTableId() {
		return CLITableConstants.CLI_TABLE_KLASS_LAYOUT;
	}

	@Override
	protected CLIKlassLayoutTableRow createNew(CLITables tables, int cursor, int rowIndex) {
		return new CLIKlassLayoutTableRow(tables, cursor, rowIndex);
	}

}
