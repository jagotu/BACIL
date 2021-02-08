package com.vztekoverflow.bacil.parser.cli.tables.generated;

import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.parser.cli.tables.CLITableRow;
import com.vztekoverflow.bacil.parser.cli.tables.CLITables;
public class CLIGenericParamConstraintTableRow extends CLITableRow<CLIGenericParamConstraintTableRow> {

	public CLIGenericParamConstraintTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	public final CLITablePtr getOwner() { 
		int offset = 0;
		final int rowNo;
		if (areSmallEnough(CLITableConstants.CLI_TABLE_GENERIC_PARAM)) {rowNo = getShort(offset);} else {rowNo = getInt(offset);}
		return new CLITablePtr(CLITableConstants.CLI_TABLE_GENERIC_PARAM, rowNo);
	}

	private static final byte[] MAP_CONSTRAINT_TABLES = new byte[] { CLITableConstants.CLI_TABLE_TYPE_DEF, CLITableConstants.CLI_TABLE_TYPE_REF, CLITableConstants.CLI_TABLE_TYPE_SPEC} ;
	public final CLITablePtr getConstraint() { 
		int offset = 2;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_GENERIC_PARAM)) offset += 2;
		int codedValue;
		if (areSmallEnough(CLITableConstants.CLI_TABLE_TYPE_DEF, CLITableConstants.CLI_TABLE_TYPE_REF, CLITableConstants.CLI_TABLE_TYPE_SPEC)) {codedValue = getShort(offset);} else {codedValue = getInt(offset);}
		return new CLITablePtr(MAP_CONSTRAINT_TABLES[codedValue & 3], codedValue >> 2);
	}

	@Override
	public int getLength() {
		int offset = 4;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_GENERIC_PARAM)) offset += 2;
		if (!areSmallEnough(CLITableConstants.CLI_TABLE_TYPE_DEF, CLITableConstants.CLI_TABLE_TYPE_REF, CLITableConstants.CLI_TABLE_TYPE_SPEC)) offset += 2;
		return offset;
	}

	@Override
	public byte getTableId() {
		return CLITableConstants.CLI_TABLE_GENERIC_PARAM_CONSTRAINT;
	}

	@Override
	protected CLIGenericParamConstraintTableRow createNew(CLITables tables, int cursor, int rowIndex) {
		return new CLIGenericParamConstraintTableRow(tables, cursor, rowIndex);
	}

}
