package com.vztekoverflow.bacil.parser.cli.tables.generated;

import com.vztekoverflow.bacil.parser.cli.tables.*;
public class CLIAssemblyRefOSTableRow extends CLITableRow<CLIAssemblyRefOSTableRow> {

	public CLIAssemblyRefOSTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	public final int getOSPlatformId() {
		int offset = 0;
		return getInt(offset);
	}

	public final int getOSMajorVersion() {
		int offset = 4;
		return getInt(offset);
	}

	public final int getOSMinorVersion() {
		int offset = 8;
		return getInt(offset);
	}

	public final CLITablePtr getAssemblyRef() { 
		int offset = 12;
		return new CLITablePtr(CLITableConstants.CLI_TABLE_ASSEMBLY_REF, getShort(offset));
	}

	@Override
	public int getLength() {
		int offset = 14;
		return offset;
	}

	@Override
	public byte getTableId() {
		return CLITableConstants.CLI_TABLE_ASSEMBLY_REF_OS;
	}

	@Override
	protected CLIAssemblyRefOSTableRow createNew(CLITables tables, int cursor, int rowIndex) {
		return new CLIAssemblyRefOSTableRow(tables, cursor, rowIndex);
	}

}
