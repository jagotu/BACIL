package com.vztekoverflow.bacil.parser.cli.tables.generated;

import com.vztekoverflow.bacil.parser.cli.tables.*;
public class CLINestedKlassTableRow extends CLITableRow<CLINestedKlassTableRow> {

	public CLINestedKlassTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	public final CLITablePtr getNestedKlass() { 
		int offset = 0;
		return new CLITablePtr(CLITableConstants.CLI_TABLE_TYPE_DEF, getShort(offset));
	}

	public final CLITablePtr getEnclosingKlass() { 
		int offset = 2;
		return new CLITablePtr(CLITableConstants.CLI_TABLE_TYPE_DEF, getShort(offset));
	}

	@Override
	public int getLength() {
		int offset = 4;
		return offset;
	}

	@Override
	public byte getTableId() {
		return CLITableConstants.CLI_TABLE_NESTED_KLASS;
	}

	@Override
	protected CLINestedKlassTableRow createNew(CLITables tables, int cursor, int rowIndex) {
		return new CLINestedKlassTableRow(tables, cursor, rowIndex);
	}

}
