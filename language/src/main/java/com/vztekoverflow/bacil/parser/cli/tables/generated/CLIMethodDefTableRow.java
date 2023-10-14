package com.vztekoverflow.bacil.parser.cli.tables.generated;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.parser.cli.tables.*;
public class CLIMethodDefTableRow extends CLITableRow<CLIMethodDefTableRow> {

	public CLIMethodDefTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	public final int getRVA() {
		int offset = 0;
		return getInt(offset);
	}

	public final short getImplFlags() {
		int offset = 4;
		return getShort(offset);
	}

	public final short getFlags() {
		int offset = 6;
		return getShort(offset);
	}

	public final CLIStringHeapPtr getName() {
		int offset = 8;
		int heapOffset=0;
		if (tables.isStringHeapBig()) { heapOffset = getInt(offset); } else { heapOffset = getUShort(offset); }
		return new CLIStringHeapPtr(heapOffset);
	}

	public final CLIBlobHeapPtr getSignature() {
		int offset = 10;
		if (tables.isStringHeapBig()) offset += 2;
		int heapOffset=0;
		if (tables.isBlobHeapBig()) { heapOffset = getInt(offset); } else { heapOffset = getUShort(offset); }
		return new CLIBlobHeapPtr(heapOffset);
	}

	@CompilerDirectives.CompilationFinal(dimensions = 1)
	private static final byte[] MAP_PARAM_LIST_TABLES = new byte[] {CLITableConstants.CLI_TABLE_PARAM};
	public final CLITablePtr getParamList() { 
		int offset = 12;
		if (tables.isStringHeapBig()) offset += 2;
		if (tables.isBlobHeapBig()) offset += 2;
		final int rowNo;
		if (areSmallEnough(MAP_PARAM_LIST_TABLES)) {rowNo = getShort(offset);} else {rowNo = getInt(offset);}
		return new CLITablePtr(CLITableConstants.CLI_TABLE_PARAM, rowNo);
	}

	@Override
	public int getLength() {
		int offset = 14;
		if (tables.isStringHeapBig()) offset += 2;
		if (tables.isBlobHeapBig()) offset += 2;
		if (!areSmallEnough(MAP_PARAM_LIST_TABLES)) offset += 2;
		return offset;
	}

	@Override
	public byte getTableId() {
		return CLITableConstants.CLI_TABLE_METHOD_DEF;
	}

	@Override
	protected CLIMethodDefTableRow createNew(CLITables tables, int cursor, int rowIndex) {
		return new CLIMethodDefTableRow(tables, cursor, rowIndex);
	}

}
