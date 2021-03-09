package com.vztekoverflow.bacil.parser.cli.tables.generated;

import com.vztekoverflow.bacil.parser.cli.tables.CLIBlobHeapPtr;
import com.vztekoverflow.bacil.parser.cli.tables.CLIStringHeapPtr;
import com.vztekoverflow.bacil.parser.cli.tables.CLITableRow;
import com.vztekoverflow.bacil.parser.cli.tables.CLITables;
public class CLIAssemblyTableRow extends CLITableRow<CLIAssemblyTableRow> {

	public CLIAssemblyTableRow(CLITables tables, int cursor, int rowIndex) {
		super(tables, cursor, rowIndex);
	}

	public final int getHashAlgId() {
		int offset = 0;
		return getInt(offset);
	}

	public final short getMajorVersion() {
		int offset = 4;
		return getShort(offset);
	}

	public final short getMinorVersion() {
		int offset = 6;
		return getShort(offset);
	}

	public final short getBuildNumber() {
		int offset = 8;
		return getShort(offset);
	}

	public final short getRevisionNumber() {
		int offset = 10;
		return getShort(offset);
	}

	public final int getFlags() {
		int offset = 12;
		return getInt(offset);
	}

	public final CLIBlobHeapPtr getPublicKey() {
		int offset = 16;
		int heapOffset=0;
		if (tables.isBlobHeapBig()) { heapOffset = getInt(offset); } else { heapOffset = getUShort(offset); }
		return new CLIBlobHeapPtr(heapOffset);
	}

	public final CLIStringHeapPtr getName() {
		int offset = 18;
		if (tables.isBlobHeapBig()) offset += 2;
		int heapOffset=0;
		if (tables.isStringHeapBig()) { heapOffset = getInt(offset); } else { heapOffset = getUShort(offset); }
		return new CLIStringHeapPtr(heapOffset);
	}

	public final CLIStringHeapPtr getCulture() {
		int offset = 20;
		if (tables.isStringHeapBig()) offset += 2;
		if (tables.isBlobHeapBig()) offset += 2;
		int heapOffset=0;
		if (tables.isStringHeapBig()) { heapOffset = getInt(offset); } else { heapOffset = getUShort(offset); }
		return new CLIStringHeapPtr(heapOffset);
	}

	@Override
	public int getLength() {
		int offset = 22;
		if (tables.isStringHeapBig()) offset += 4;
		if (tables.isBlobHeapBig()) offset += 2;
		return offset;
	}

	@Override
	public byte getTableId() {
		return CLITableConstants.CLI_TABLE_ASSEMBLY;
	}

	@Override
	protected CLIAssemblyTableRow createNew(CLITables tables, int cursor, int rowIndex) {
		return new CLIAssemblyTableRow(tables, cursor, rowIndex);
	}

}
