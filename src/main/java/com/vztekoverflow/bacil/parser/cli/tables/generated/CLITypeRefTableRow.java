package com.vztekoverflow.bacil.parser.cli.tables.generated;

import com.vztekoverflow.bacil.parser.cli.tables.*;
public class CLITypeRefTableRow extends CLITableRow<CLITypeRefTableRow> {

	public CLITypeRefTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	private static final byte[] MAP_RESOLUTION_SCOPE_TABLES = new byte[] { CLITableConstants.CLI_TABLE_MODULE, CLITableConstants.CLI_TABLE_MODULE_REF, CLITableConstants.CLI_TABLE_ASSEMBLY_REF, CLITableConstants.CLI_TABLE_TYPE_REF} ;
	public final CLITablePtr getResolutionScope() { 
		int offset = 0;
		short codedValue = getShort(offset);
		return new CLITablePtr(MAP_RESOLUTION_SCOPE_TABLES[codedValue & 3], codedValue >> 2);
	}

	public final CLIStringHeapPtr getTypeName() {
		int offset = 2;
		int heapOffset=0;
		if (tables.isStringHeapBig()) { heapOffset = getInt(offset); } else { heapOffset = getShort(offset); }
		return new CLIStringHeapPtr(heapOffset);
	}

	public final CLIStringHeapPtr getTypeNamespace() {
		int offset = 4;
		if (tables.isStringHeapBig()) offset += 2;
		int heapOffset=0;
		if (tables.isStringHeapBig()) { heapOffset = getInt(offset); } else { heapOffset = getShort(offset); }
		return new CLIStringHeapPtr(heapOffset);
	}

	@Override
	public int getLength() {
		int offset = 6;
		if (tables.isStringHeapBig()) offset += 4;
		return offset;
	}

	@Override
	public byte getTableId() {
		return CLITableConstants.CLI_TABLE_TYPE_REF;
	}

	@Override
	protected CLITypeRefTableRow createNew(CLITables tables, int cursor, int rowIndex) {
		return new CLITypeRefTableRow(tables, cursor, rowIndex);
	}

}
