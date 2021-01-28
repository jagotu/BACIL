package com.vztekoverflow.bacil.parser.cli.tables.generated;

import com.vztekoverflow.bacil.parser.cli.tables.*;
public class CLITypeDefTableRow extends CLITableRow<CLITypeDefTableRow> {

	public CLITypeDefTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	public final int getFlags() {
		int offset = 0;
		return getInt(offset);
	}

	public final CLIStringHeapPtr getTypeName() {
		int offset = 4;
		int heapOffset=0;
		if (tables.isStringHeapBig()) { heapOffset = getInt(offset); } else { heapOffset = getShort(offset); }
		return new CLIStringHeapPtr(heapOffset);
	}

	public final CLIStringHeapPtr getTypeNamespace() {
		int offset = 6;
		if (tables.isStringHeapBig()) offset += 2;
		int heapOffset=0;
		if (tables.isStringHeapBig()) { heapOffset = getInt(offset); } else { heapOffset = getShort(offset); }
		return new CLIStringHeapPtr(heapOffset);
	}

	private static final byte[] MAP_EXTENDS_TABLES = new byte[] { CLITableConstants.CLI_TABLE_TYPE_DEF, CLITableConstants.CLI_TABLE_TYPE_REF, CLITableConstants.CLI_TABLE_TYPE_SPEC} ;
	public final CLITablePtr getExtends() { 
		int offset = 8;
		if (tables.isStringHeapBig()) offset += 4;
		int codedValue;
		if (areSmallEnough(CLITableConstants.CLI_TABLE_TYPE_DEF, CLITableConstants.CLI_TABLE_TYPE_REF, CLITableConstants.CLI_TABLE_TYPE_SPEC)) {codedValue = getShort(offset);} else {codedValue = getInt(offset);}
		return new CLITablePtr(MAP_EXTENDS_TABLES[codedValue & 3], codedValue >> 2);
	}

	public final CLITablePtr getFieldList() { 
		int offset = 10;
		if (tables.isStringHeapBig()) offset += 4;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_TYPE_DEF, CLITableConstants.CLI_TABLE_TYPE_REF, CLITableConstants.CLI_TABLE_TYPE_SPEC)) offset += 2;
		final int rowNo;
		if (areSmallEnough(CLITableConstants.CLI_TABLE_FIELD)) {rowNo = getShort(offset);} else {rowNo = getInt(offset);}
		return new CLITablePtr(CLITableConstants.CLI_TABLE_FIELD, rowNo);
	}

	public final CLITablePtr getMethodList() { 
		int offset = 12;
		if (tables.isStringHeapBig()) offset += 4;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_TYPE_DEF, CLITableConstants.CLI_TABLE_TYPE_REF, CLITableConstants.CLI_TABLE_TYPE_SPEC)) offset += 2;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_FIELD)) offset += 2;
		final int rowNo;
		if (areSmallEnough(CLITableConstants.CLI_TABLE_METHOD_DEF)) {rowNo = getShort(offset);} else {rowNo = getInt(offset);}
		return new CLITablePtr(CLITableConstants.CLI_TABLE_METHOD_DEF, rowNo);
	}

	@Override
	public int getLength() {
		int offset = 14;
		if (tables.isStringHeapBig()) offset += 4;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_TYPE_DEF, CLITableConstants.CLI_TABLE_TYPE_REF, CLITableConstants.CLI_TABLE_TYPE_SPEC)) offset += 2;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_FIELD)) offset += 2;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_METHOD_DEF)) offset += 2;
		return offset;
	}

	@Override
	public byte getTableId() {
		return CLITableConstants.CLI_TABLE_TYPE_DEF;
	}

	@Override
	protected CLITypeDefTableRow createNew(CLITables tables, int cursor, int rowIndex) {
		return new CLITypeDefTableRow(tables, cursor, rowIndex);
	}

}
