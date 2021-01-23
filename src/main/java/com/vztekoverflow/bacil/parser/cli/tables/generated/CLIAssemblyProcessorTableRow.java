package com.vztekoverflow.bacil.parser.cli.tables.generated;

import com.vztekoverflow.bacil.parser.cli.tables.*;
public class CLIAssemblyProcessorTableRow extends CLITableRow<CLIAssemblyProcessorTableRow> {

	public CLIAssemblyProcessorTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	public final int getProcessor() {
		int offset = 0;
		return getInt(offset);
	}

	@Override
	public int getLength() {
		int offset = 4;
		return offset;
	}

	@Override
	public byte getTableId() {
		return CLITableConstants.CLI_TABLE_ASSEMBLY_PROCESSOR;
	}

	@Override
	protected CLIAssemblyProcessorTableRow createNew(CLITables tables, int cursor, int rowIndex) {
		return new CLIAssemblyProcessorTableRow(tables, cursor, rowIndex);
	}

}
