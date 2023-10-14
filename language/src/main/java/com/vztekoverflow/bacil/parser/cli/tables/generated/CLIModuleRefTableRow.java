package com.vztekoverflow.bacil.parser.cli.tables.generated;

import com.vztekoverflow.bacil.parser.cli.tables.CLIStringHeapPtr;
import com.vztekoverflow.bacil.parser.cli.tables.CLITableRow;
import com.vztekoverflow.bacil.parser.cli.tables.CLITables;
public class CLIModuleRefTableRow extends CLITableRow<CLIModuleRefTableRow> {

	public CLIModuleRefTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	public final CLIStringHeapPtr getName() {
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
