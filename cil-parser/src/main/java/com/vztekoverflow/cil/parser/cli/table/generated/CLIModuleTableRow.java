package com.vztekoverflow.cil.parser.cli.table.generated;

import com.vztekoverflow.cil.parser.cli.table.CLIGUIDHeapPtr;
import com.vztekoverflow.cil.parser.cli.table.CLIStringHeapPtr;
import com.vztekoverflow.cil.parser.cli.table.CLITableRow;
import com.vztekoverflow.cil.parser.cli.table.CLITables;
public class CLIModuleTableRow extends CLITableRow<CLIModuleTableRow> {

	public CLIModuleTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	public final short getGeneration() {
		int offset = 0;
		return getShort(offset);
	}

	public final CLIStringHeapPtr getNameHeapPtr() {
		int offset = 2;
		int heapOffset=0;
		if (tables.isStringHeapBig()) { heapOffset = getInt(offset); } else { heapOffset = getUShort(offset); }
		return new CLIStringHeapPtr(heapOffset);
	}

	public final CLIGUIDHeapPtr getMvidHeapPtr() {
		int offset = 4;
		if (tables.isStringHeapBig()) offset += 2;
		int heapOffset=0;
		if (tables.isGUIDHeapBig()) { heapOffset = getInt(offset); } else { heapOffset = getUShort(offset); }
		return new CLIGUIDHeapPtr(heapOffset);
	}

	public final CLIGUIDHeapPtr getEncIdHeapPtr() {
		int offset = 6;
		if (tables.isStringHeapBig()) offset += 2;
		if (tables.isGUIDHeapBig()) offset += 2;
		int heapOffset=0;
		if (tables.isGUIDHeapBig()) { heapOffset = getInt(offset); } else { heapOffset = getUShort(offset); }
		return new CLIGUIDHeapPtr(heapOffset);
	}

	public final CLIGUIDHeapPtr getEncBaseIdHeapPtr() {
		int offset = 8;
		if (tables.isStringHeapBig()) offset += 2;
		if (tables.isGUIDHeapBig()) offset += 4;
		int heapOffset=0;
		if (tables.isGUIDHeapBig()) { heapOffset = getInt(offset); } else { heapOffset = getUShort(offset); }
		return new CLIGUIDHeapPtr(heapOffset);
	}

	@Override
	public int getLength() {
		int offset = 10;
		if (tables.isStringHeapBig()) offset += 2;
		if (tables.isGUIDHeapBig()) offset += 6;
		return offset;
	}

	@Override
	public byte getTableId() {
		return CLITableConstants.CLI_TABLE_MODULE;
	}

	@Override
	protected CLIModuleTableRow createNew(CLITables tables, int cursor, int rowIndex) {
		return new CLIModuleTableRow(tables, cursor, rowIndex);
	}

}
