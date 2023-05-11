package com.vztekoverflow.cil.parser.cli.table.generated;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.cil.parser.cli.table.*;
public class CLIFieldTableRow extends CLITableRow<CLIFieldTableRow> {

	public CLIFieldTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	public final short getFlags() {
		int offset = 0;
		return getShort(offset);
	}

	public final CLIStringHeapPtr getNameHeapPtr() {
		int offset = 2;
		int heapOffset=0;
		if (tables.isStringHeapBig()) { heapOffset = getInt(offset); } else { heapOffset = getUShort(offset); }
		return new CLIStringHeapPtr(heapOffset);
	}

	public final CLIBlobHeapPtr getSignatureHeapPtr() {
		int offset = 4;
		if (tables.isStringHeapBig()) offset += 2;
		int heapOffset=0;
		if (tables.isBlobHeapBig()) { heapOffset = getInt(offset); } else { heapOffset = getUShort(offset); }
		return new CLIBlobHeapPtr(heapOffset);
	}

	@Override
	public int getLength() {
		int offset = 6;
		if (tables.isStringHeapBig()) offset += 2;
		if (tables.isBlobHeapBig()) offset += 2;
		return offset;
	}

	@Override
	public byte getTableId() {
		return CLITableConstants.CLI_TABLE_FIELD;
	}

	@Override
	protected CLIFieldTableRow createNew(CLITables tables, int cursor, int rowIndex) {
		return new CLIFieldTableRow(tables, cursor, rowIndex);
	}

}
