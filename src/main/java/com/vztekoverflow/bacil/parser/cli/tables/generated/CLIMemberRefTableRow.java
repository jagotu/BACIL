package com.vztekoverflow.bacil.parser.cli.tables.generated;

import com.vztekoverflow.bacil.parser.cli.tables.*;
public class CLIMemberRefTableRow extends CLITableRow<CLIMemberRefTableRow> {

	public CLIMemberRefTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	private static final byte[] MAP_KLASS_TABLES = new byte[] { CLITableConstants.CLI_TABLE_TYPE_DEF, CLITableConstants.CLI_TABLE_TYPE_REF, CLITableConstants.CLI_TABLE_MODULE_REF, CLITableConstants.CLI_TABLE_METHOD_DEF, CLITableConstants.CLI_TABLE_TYPE_SPEC} ;
	public final CLITablePtr getKlass() { 
		int offset = 0;
		short codedValue = getShort(offset);
		return new CLITablePtr(MAP_KLASS_TABLES[codedValue & 7], codedValue >> 3);
	}

	public final CLIStringHeapPtr getName() {
		int offset = 2;
		int heapOffset=0;
		if (tables.isStringHeapBig()) { heapOffset = getInt(offset); } else { heapOffset = getShort(offset); }
		return new CLIStringHeapPtr(heapOffset);
	}

	public final CLIBlobHeapPtr getSignature() {
		int offset = 4;
		if (tables.isStringHeapBig()) offset += 2;
		int heapOffset=0;
		if (tables.isBlobHeapBig()) { heapOffset = getInt(offset); } else { heapOffset = getShort(offset); }
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
		return CLITableConstants.CLI_TABLE_MEMBER_REF;
	}

	@Override
	protected CLIMemberRefTableRow createNew(CLITables tables, int cursor, int rowIndex) {
		return new CLIMemberRefTableRow(tables, cursor, rowIndex);
	}

}
