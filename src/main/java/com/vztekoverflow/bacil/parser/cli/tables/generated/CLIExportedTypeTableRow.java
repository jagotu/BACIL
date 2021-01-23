package com.vztekoverflow.bacil.parser.cli.tables.generated;

import com.vztekoverflow.bacil.parser.cli.tables.*;
public class CLIExportedTypeTableRow extends CLITableRow<CLIExportedTypeTableRow> {

	public CLIExportedTypeTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	public final int getFlags() {
		int offset = 0;
		return getInt(offset);
	}

	public final int getTypeDefId() {
		int offset = 4;
		return getInt(offset);
	}

	public final CLIStringHeapPtr getTypeName() {
		int offset = 8;
		int heapOffset=0;
		if (tables.isStringHeapBig()) { heapOffset = getInt(offset); } else { heapOffset = getShort(offset); }
		return new CLIStringHeapPtr(heapOffset);
	}

	public final CLIStringHeapPtr getTypeNamespace() {
		int offset = 10;
		if (tables.isStringHeapBig()) offset += 2;
		int heapOffset=0;
		if (tables.isStringHeapBig()) { heapOffset = getInt(offset); } else { heapOffset = getShort(offset); }
		return new CLIStringHeapPtr(heapOffset);
	}

	private static final byte[] MAP_IMPLEMENTATION_TABLES = new byte[] { CLITableConstants.CLI_TABLE_FILE, CLITableConstants.CLI_TABLE_ASSEMBLY_REF, CLITableConstants.CLI_TABLE_EXPORTED_TYPE} ;
	public final CLITablePtr getImplementation() { 
		int offset = 12;
		if (tables.isStringHeapBig()) offset += 4;
		short codedValue = getShort(offset);
		return new CLITablePtr(MAP_IMPLEMENTATION_TABLES[codedValue & 3], codedValue >> 2);
	}

	@Override
	public int getLength() {
		int offset = 14;
		if (tables.isStringHeapBig()) offset += 4;
		return offset;
	}

	@Override
	public byte getTableId() {
		return CLITableConstants.CLI_TABLE_EXPORTED_TYPE;
	}

	@Override
	protected CLIExportedTypeTableRow createNew(CLITables tables, int cursor, int rowIndex) {
		return new CLIExportedTypeTableRow(tables, cursor, rowIndex);
	}

}
