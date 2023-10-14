package com.vztekoverflow.bacil.parser.cli.tables.generated;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.parser.cli.tables.CLIBlobHeapPtr;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.parser.cli.tables.CLITableRow;
import com.vztekoverflow.bacil.parser.cli.tables.CLITables;
public class CLICustomAttributeTableRow extends CLITableRow<CLICustomAttributeTableRow> {

	public CLICustomAttributeTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	@CompilerDirectives.CompilationFinal(dimensions = 1)
	private static final byte[] MAP_PARENT_TABLES = new byte[] { CLITableConstants.CLI_TABLE_METHOD_DEF, CLITableConstants.CLI_TABLE_FIELD, CLITableConstants.CLI_TABLE_TYPE_REF, CLITableConstants.CLI_TABLE_TYPE_DEF, CLITableConstants.CLI_TABLE_PARAM, CLITableConstants.CLI_TABLE_INTERFACE_IMPL, CLITableConstants.CLI_TABLE_MEMBER_REF, CLITableConstants.CLI_TABLE_MODULE, CLITableConstants.CLI_TABLE_DECL_SECURITY, CLITableConstants.CLI_TABLE_PROPERTY, CLITableConstants.CLI_TABLE_EVENT, CLITableConstants.CLI_TABLE_STAND_ALONE_SIG, CLITableConstants.CLI_TABLE_MODULE_REF, CLITableConstants.CLI_TABLE_TYPE_SPEC, CLITableConstants.CLI_TABLE_ASSEMBLY, CLITableConstants.CLI_TABLE_ASSEMBLY_REF, CLITableConstants.CLI_TABLE_FILE, CLITableConstants.CLI_TABLE_EXPORTED_TYPE, CLITableConstants.CLI_TABLE_MANIFEST_RESOURCE, CLITableConstants.CLI_TABLE_GENERIC_PARAM, CLITableConstants.CLI_TABLE_GENERIC_PARAM_CONSTRAINT, CLITableConstants.CLI_TABLE_METHOD_SPEC} ;
	public final CLITablePtr getParent() { 
		int offset = 0;
		int codedValue;
		if (areSmallEnough(MAP_PARENT_TABLES)) {codedValue = getShort(offset);} else {codedValue = getInt(offset);}
		return new CLITablePtr(MAP_PARENT_TABLES[codedValue & 31], codedValue >> 5);
	}

	@CompilerDirectives.CompilationFinal(dimensions = 1)
	private static final byte[] MAP_TYPE_TABLES = new byte[] { CLITableConstants.CLI_TABLE_NONE_ID, CLITableConstants.CLI_TABLE_NONE_ID, CLITableConstants.CLI_TABLE_METHOD_DEF, CLITableConstants.CLI_TABLE_MEMBER_REF} ;
	public final CLITablePtr getType() { 
		int offset = 2;
		if (!areSmallEnough(MAP_PARENT_TABLES)) offset += 2;
		int codedValue;
		if (areSmallEnough(MAP_TYPE_TABLES)) {codedValue = getShort(offset);} else {codedValue = getInt(offset);}
		return new CLITablePtr(MAP_TYPE_TABLES[codedValue & 3], codedValue >> 2);
	}

	public final CLIBlobHeapPtr getValue() {
		int offset = 4;
		if (!areSmallEnough(MAP_PARENT_TABLES)) offset += 2;
		if (!areSmallEnough(MAP_TYPE_TABLES)) offset += 2;
		int heapOffset=0;
		if (tables.isBlobHeapBig()) { heapOffset = getInt(offset); } else { heapOffset = getUShort(offset); }
		return new CLIBlobHeapPtr(heapOffset);
	}

	@Override
	public int getLength() {
		int offset = 6;
		if (tables.isBlobHeapBig()) offset += 2;
		if (!areSmallEnough(MAP_PARENT_TABLES)) offset += 2;
		if (!areSmallEnough(MAP_TYPE_TABLES)) offset += 2;
		return offset;
	}

	@Override
	public byte getTableId() {
		return CLITableConstants.CLI_TABLE_CUSTOM_ATTRIBUTE;
	}

	@Override
	protected CLICustomAttributeTableRow createNew(CLITables tables, int cursor, int rowIndex) {
		return new CLICustomAttributeTableRow(tables, cursor, rowIndex);
	}

}
