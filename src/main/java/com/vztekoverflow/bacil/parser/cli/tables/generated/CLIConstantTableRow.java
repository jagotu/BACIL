package com.vztekoverflow.bacil.parser.cli.tables.generated;

import com.vztekoverflow.bacil.parser.cli.tables.*;
public class CLIConstantTableRow extends CLITableRow<CLIConstantTableRow> {

	public CLIConstantTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	public final byte getType() {
		int offset = 0;
		return getByte(offset);
	}

	private static final byte[] MAP_PARENT_TABLES = new byte[] { CLITableConstants.CLI_TABLE_FIELD, CLITableConstants.CLI_TABLE_PARAM, CLITableConstants.CLI_TABLE_PROPERTY} ;
	public final CLITablePtr getParent() { 
		int offset = 2;
		int codedValue;
		if (areSmallEnough(CLITableConstants.CLI_TABLE_FIELD, CLITableConstants.CLI_TABLE_PARAM, CLITableConstants.CLI_TABLE_PROPERTY)) {codedValue = getShort(offset);} else {codedValue = getInt(offset);}
		return new CLITablePtr(MAP_PARENT_TABLES[codedValue & 3], codedValue >> 2);
	}

	public final CLIBlobHeapPtr getValue() {
		int offset = 4;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_FIELD, CLITableConstants.CLI_TABLE_PARAM, CLITableConstants.CLI_TABLE_PROPERTY)) offset += 2;
		int heapOffset=0;
		if (tables.isBlobHeapBig()) { heapOffset = getInt(offset); } else { heapOffset = getShort(offset); }
		return new CLIBlobHeapPtr(heapOffset);
	}

	@Override
	public int getLength() {
		int offset = 6;
		if (tables.isBlobHeapBig()) offset += 2;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_FIELD, CLITableConstants.CLI_TABLE_PARAM, CLITableConstants.CLI_TABLE_PROPERTY)) offset += 2;
		return offset;
	}

	@Override
	public byte getTableId() {
		return CLITableConstants.CLI_TABLE_CONSTANT;
	}

	@Override
	protected CLIConstantTableRow createNew(CLITables tables, int cursor, int rowIndex) {
		return new CLIConstantTableRow(tables, cursor, rowIndex);
	}

}
