package com.vztekoverflow.bacil.parser.cli.tables.generated;

import com.vztekoverflow.bacil.parser.cli.tables.*;
public class CLIEventTableRow extends CLITableRow<CLIEventTableRow> {

	public CLIEventTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	public final short getEventFlags() {
		int offset = 0;
		return getShort(offset);
	}

	public final CLIStringHeapPtr getName() {
		int offset = 2;
		int heapOffset=0;
		if (tables.isStringHeapBig()) { heapOffset = getInt(offset); } else { heapOffset = getShort(offset); }
		return new CLIStringHeapPtr(heapOffset);
	}

	private static final byte[] MAP_EVENT_TYPE_TABLES = new byte[] { CLITableConstants.CLI_TABLE_TYPE_DEF, CLITableConstants.CLI_TABLE_TYPE_REF, CLITableConstants.CLI_TABLE_TYPE_SPEC} ;
	public final CLITablePtr getEventType() { 
		int offset = 4;
		if (tables.isStringHeapBig()) offset += 2;
		int codedValue;
		if (areSmallEnough(CLITableConstants.CLI_TABLE_TYPE_DEF, CLITableConstants.CLI_TABLE_TYPE_REF, CLITableConstants.CLI_TABLE_TYPE_SPEC)) {codedValue = getShort(offset);} else {codedValue = getInt(offset);}
		return new CLITablePtr(MAP_EVENT_TYPE_TABLES[codedValue & 3], codedValue >> 2);
	}

	@Override
	public int getLength() {
		int offset = 6;
		if (tables.isStringHeapBig()) offset += 2;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_TYPE_DEF, CLITableConstants.CLI_TABLE_TYPE_REF, CLITableConstants.CLI_TABLE_TYPE_SPEC)) offset += 2;
		return offset;
	}

	@Override
	public byte getTableId() {
		return CLITableConstants.CLI_TABLE_EVENT;
	}

	@Override
	protected CLIEventTableRow createNew(CLITables tables, int cursor, int rowIndex) {
		return new CLIEventTableRow(tables, cursor, rowIndex);
	}

}
