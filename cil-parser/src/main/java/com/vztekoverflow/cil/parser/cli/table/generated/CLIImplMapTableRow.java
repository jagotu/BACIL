package com.vztekoverflow.cil.parser.cli.table.generated;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.cil.parser.cli.table.CLIStringHeapPtr;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.CLITableRow;
import com.vztekoverflow.cil.parser.cli.table.CLITables;
public class CLIImplMapTableRow extends CLITableRow<CLIImplMapTableRow> {

	public CLIImplMapTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	public final short getMappingFlags() {
		int offset = 0;
		return getShort(offset);
	}

	@CompilerDirectives.CompilationFinal(dimensions = 1)
	private static final byte[] MAP_MEMBER_FORWARDED_TABLES = new byte[] { CLITableConstants.CLI_TABLE_FIELD, CLITableConstants.CLI_TABLE_METHOD_DEF} ;
	public final CLITablePtr getMemberForwardedTablePtr() {
        int offset = 2;
        int codedValue;
        var isSmall = areSmallEnough(MAP_MEMBER_FORWARDED_TABLES);
        if (isSmall) {
            codedValue = getShort(offset);
        } else {
            codedValue = getInt(offset);
        }
        if ((isSmall && (codedValue & 0xffff) == 0xffff) || (!isSmall && (codedValue & 0xffffffff) == 0xffffffff))
            return null;
        return new CLITablePtr(MAP_MEMBER_FORWARDED_TABLES[codedValue & 1], codedValue >> 1);
    }

	public final CLIStringHeapPtr getImportNameHeapPtr() {
		int offset = 4;
		if (!areSmallEnough(MAP_MEMBER_FORWARDED_TABLES)) offset += 2;
		int heapOffset=0;
		if (tables.isStringHeapBig()) { heapOffset = getInt(offset); } else { heapOffset = getUShort(offset); }
		return new CLIStringHeapPtr(heapOffset);
	}

	@CompilerDirectives.CompilationFinal(dimensions = 1)
	private static final byte[] MAP_IMPORT_SCOPE_TABLES = new byte[] {CLITableConstants.CLI_TABLE_MODULE_REF};
	public final CLITablePtr getImportScopeTablePtr() { 
		int offset = 6;
		if (tables.isStringHeapBig()) offset += 2;
		if (!areSmallEnough(MAP_MEMBER_FORWARDED_TABLES)) offset += 2;
		final int rowNo;
		if (areSmallEnough(MAP_IMPORT_SCOPE_TABLES)) {rowNo = getShort(offset);} else {rowNo = getInt(offset);}
		return new CLITablePtr(CLITableConstants.CLI_TABLE_MODULE_REF, rowNo);
	}

	@Override
	public int getLength() {
		int offset = 8;
		if (tables.isStringHeapBig()) offset += 2;
		if (!areSmallEnough(MAP_MEMBER_FORWARDED_TABLES)) offset += 2;
		if (!areSmallEnough(MAP_IMPORT_SCOPE_TABLES)) offset += 2;
		return offset;
	}

	@Override
	public byte getTableId() {
		return CLITableConstants.CLI_TABLE_IMPL_MAP;
	}

	@Override
	protected CLIImplMapTableRow createNew(CLITables tables, int cursor, int rowIndex) {
		return new CLIImplMapTableRow(tables, cursor, rowIndex);
	}

}
