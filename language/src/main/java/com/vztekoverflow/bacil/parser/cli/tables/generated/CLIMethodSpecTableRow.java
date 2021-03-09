package com.vztekoverflow.bacil.parser.cli.tables.generated;

import com.vztekoverflow.bacil.parser.cli.tables.CLIBlobHeapPtr;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.parser.cli.tables.CLITableRow;
import com.vztekoverflow.bacil.parser.cli.tables.CLITables;
public class CLIMethodSpecTableRow extends CLITableRow<CLIMethodSpecTableRow> {

	public CLIMethodSpecTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	private static final byte[] MAP_METHOD_TABLES = new byte[] { CLITableConstants.CLI_TABLE_METHOD_DEF, CLITableConstants.CLI_TABLE_MEMBER_REF} ;
	public final CLITablePtr getMethod() { 
		int offset = 0;
		int codedValue;
		if (areSmallEnough(CLITableConstants.CLI_TABLE_METHOD_DEF, CLITableConstants.CLI_TABLE_MEMBER_REF)) {codedValue = getShort(offset);} else {codedValue = getInt(offset);}
		return new CLITablePtr(MAP_METHOD_TABLES[codedValue & 1], codedValue >> 1);
	}

	public final CLIBlobHeapPtr getInstantiation() {
		int offset = 2;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_METHOD_DEF, CLITableConstants.CLI_TABLE_MEMBER_REF)) offset += 2;
		int heapOffset=0;
		if (tables.isBlobHeapBig()) { heapOffset = getInt(offset); } else { heapOffset = getUShort(offset); }
		return new CLIBlobHeapPtr(heapOffset);
	}

	@Override
	public int getLength() {
		int offset = 4;
		if (tables.isBlobHeapBig()) offset += 2;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_METHOD_DEF, CLITableConstants.CLI_TABLE_MEMBER_REF)) offset += 2;
		return offset;
	}

	@Override
	public byte getTableId() {
		return CLITableConstants.CLI_TABLE_METHOD_SPEC;
	}

	@Override
	protected CLIMethodSpecTableRow createNew(CLITables tables, int cursor, int rowIndex) {
		return new CLIMethodSpecTableRow(tables, cursor, rowIndex);
	}

}
