package com.vztekoverflow.bacil.parser.cli.tables.generated;

import com.vztekoverflow.bacil.parser.cli.tables.CLIBlobHeapPtr;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.parser.cli.tables.CLITableRow;
import com.vztekoverflow.bacil.parser.cli.tables.CLITables;
public class CLIFieldMarshalTableRow extends CLITableRow<CLIFieldMarshalTableRow> {

	public CLIFieldMarshalTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	private static final byte[] MAP_PARENT_TABLES = new byte[] { CLITableConstants.CLI_TABLE_FIELD, CLITableConstants.CLI_TABLE_PARAM} ;
	public final CLITablePtr getParent() { 
		int offset = 0;
		int codedValue;
		if (areSmallEnough(CLITableConstants.CLI_TABLE_FIELD, CLITableConstants.CLI_TABLE_PARAM)) {codedValue = getShort(offset);} else {codedValue = getInt(offset);}
		return new CLITablePtr(MAP_PARENT_TABLES[codedValue & 1], codedValue >> 1);
	}

	public final CLIBlobHeapPtr getNativeType() {
		int offset = 2;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_FIELD, CLITableConstants.CLI_TABLE_PARAM)) offset += 2;
		int heapOffset=0;
		if (tables.isBlobHeapBig()) { heapOffset = getInt(offset); } else { heapOffset = getUShort(offset); }
		return new CLIBlobHeapPtr(heapOffset);
	}

	@Override
	public int getLength() {
		int offset = 4;
		if (tables.isBlobHeapBig()) offset += 2;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_FIELD, CLITableConstants.CLI_TABLE_PARAM)) offset += 2;
		return offset;
	}

	@Override
	public byte getTableId() {
		return CLITableConstants.CLI_TABLE_FIELD_MARSHAL;
	}

	@Override
	protected CLIFieldMarshalTableRow createNew(CLITables tables, int cursor, int rowIndex) {
		return new CLIFieldMarshalTableRow(tables, cursor, rowIndex);
	}

}
