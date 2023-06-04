package com.vztekoverflow.cil.parser.cli.table.generated;

import com.vztekoverflow.cil.parser.cli.table.CLIStringHeapPtr;
import com.vztekoverflow.cil.parser.cli.table.CLITableRow;
import com.vztekoverflow.cil.parser.cli.table.CLITables;
public class CLIModuleRefTableRow extends CLITableRow<CLIModuleRefTableRow> {

	public CLIModuleRefTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	public final CLIStringHeapPtr getNameHeapPtr() {
		int offset = 0;
		int heapOffset=0;
		if (tables.isStringHeapBig()) { heapOffset = getInt(offset); } else { heapOffset = getUShort(offset); }
		return new CLIStringHeapPtr(heapOffset);
	}

	@Override
	public int getLength() {
		int offset = 2;
		if (tables.isStringHeapBig()) offset += 2;
		return offset;
	}

	@Override
	public byte getTableId() {
		return CLITableConstants.CLI_TABLE_MODULE_REF;
	}

	@Override
	protected CLIModuleRefTableRow createNew(CLITables tables, int cursor, int rowIndex) {
		return new CLIModuleRefTableRow(tables, cursor, rowIndex);
	}

}
