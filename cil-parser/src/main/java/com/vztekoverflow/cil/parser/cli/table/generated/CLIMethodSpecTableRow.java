package com.vztekoverflow.cil.parser.cli.table.generated;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.cil.parser.cli.table.CLIBlobHeapPtr;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.CLITableRow;
import com.vztekoverflow.cil.parser.cli.table.CLITables;
public class CLIMethodSpecTableRow extends CLITableRow<CLIMethodSpecTableRow> {

	public CLIMethodSpecTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	@CompilerDirectives.CompilationFinal(dimensions = 1)
	private static final byte[] MAP_METHOD_TABLES = new byte[] { CLITableConstants.CLI_TABLE_METHOD_DEF, CLITableConstants.CLI_TABLE_MEMBER_REF} ;
	public final CLITablePtr getMethodTablePtr() {
        int offset = 0;
        int codedValue;
        var isSmall = areSmallEnough(MAP_METHOD_TABLES);
        if (isSmall) {
            codedValue = getShort(offset);
        } else {
            codedValue = getInt(offset);
        }
        if ((isSmall && (codedValue & 0xffff) == 0xffff) || (!isSmall && (codedValue & 0xffffffff) == 0xffffffff))
            return null;
        return new CLITablePtr(MAP_METHOD_TABLES[codedValue & 1], codedValue >> 1);
    }

	public final CLIBlobHeapPtr getInstantiationHeapPtr() {
		int offset = 2;
		if (!areSmallEnough(MAP_METHOD_TABLES)) offset += 2;
		int heapOffset=0;
		if (tables.isBlobHeapBig()) { heapOffset = getInt(offset); } else { heapOffset = getUShort(offset); }
		return new CLIBlobHeapPtr(heapOffset);
	}

	@Override
	public int getLength() {
		int offset = 4;
		if (tables.isBlobHeapBig()) offset += 2;
		if (!areSmallEnough(MAP_METHOD_TABLES)) offset += 2;
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
