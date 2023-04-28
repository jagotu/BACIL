package com.vztekoverflow.cil.parser.cli.table.generated;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.cil.parser.cli.table.*;
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
		if (tables.isStringHeapBig()) { heapOffset = getInt(offset); } else { heapOffset = getUShort(offset); }
		return new CLIStringHeapPtr(heapOffset);
	}

	public final CLIStringHeapPtr getTypeNamespace() {
		int offset = 6;
		if (tables.isStringHeapBig()) offset += 2;
		int heapOffset=0;
		if (tables.isStringHeapBig()) { heapOffset = getInt(offset); } else { heapOffset = getUShort(offset); }
		return new CLIStringHeapPtr(heapOffset);
	}

	@CompilerDirectives.CompilationFinal(dimensions = 1)
	private static final byte[] MAP_EXTENDS_TABLES = new byte[] { CLITableConstants.CLI_TABLE_TYPE_DEF, CLITableConstants.CLI_TABLE_TYPE_REF, CLITableConstants.CLI_TABLE_TYPE_SPEC} ;
	public final CLITablePtr getExtends() { 
		int offset = 8;
		if (tables.isStringHeapBig()) offset += 4;
		int codedValue;
		if (areSmallEnough(MAP_EXTENDS_TABLES)) {codedValue = getShort(offset);} else {codedValue = getInt(offset);}
		return new CLITablePtr(MAP_EXTENDS_TABLES[codedValue & 3], codedValue >> 2);
	}

	@CompilerDirectives.CompilationFinal(dimensions = 1)
	private static final byte[] MAP_FIELD_LIST_TABLES = new byte[] {CLITableConstants.CLI_TABLE_FIELD};
	public final CLITablePtr getFieldList() { 
		int offset = 10;
		if (tables.isStringHeapBig()) offset += 4;
		if (!areSmallEnough(MAP_EXTENDS_TABLES)) offset += 2;
		final int rowNo;
		if (areSmallEnough(MAP_FIELD_LIST_TABLES)) {rowNo = getShort(offset);} else {rowNo = getInt(offset);}
		return new CLITablePtr(CLITableConstants.CLI_TABLE_FIELD, rowNo);
	}

	@CompilerDirectives.CompilationFinal(dimensions = 1)
	private static final byte[] MAP_METHOD_LIST_TABLES = new byte[] {CLITableConstants.CLI_TABLE_METHOD_DEF};
	public final CLITablePtr getMethodList() { 
		int offset = 12;
		if (tables.isStringHeapBig()) offset += 4;
		if (!areSmallEnough(MAP_EXTENDS_TABLES)) offset += 2;
		if (!areSmallEnough(MAP_FIELD_LIST_TABLES)) offset += 2;
		final int rowNo;
		if (areSmallEnough(MAP_METHOD_LIST_TABLES)) {rowNo = getShort(offset);} else {rowNo = getInt(offset);}
		return new CLITablePtr(CLITableConstants.CLI_TABLE_METHOD_DEF, rowNo);
	}

	@Override
	public int getLength() {
		int offset = 14;
		if (tables.isStringHeapBig()) offset += 4;
		if (!areSmallEnough(MAP_EXTENDS_TABLES)) offset += 2;
		if (!areSmallEnough(MAP_FIELD_LIST_TABLES)) offset += 2;
		if (!areSmallEnough(MAP_METHOD_LIST_TABLES)) offset += 2;
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
