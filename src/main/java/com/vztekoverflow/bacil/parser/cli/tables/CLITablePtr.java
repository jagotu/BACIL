package com.vztekoverflow.bacil.parser.cli.tables;

import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITableConstants;

public class CLITablePtr {
    private final byte tableId;
    private final int rowNo;

    public CLITablePtr(byte tableId, int rowNo) {
        this.tableId = tableId;
        this.rowNo = rowNo;
    }

    public byte getTableId() {
        return tableId;
    }

    public int getRowNo() {
        return rowNo;
    }

    public static CLITablePtr fromToken(int token)
    {
        byte table = (byte)(token >> 24);
        int rowNo = token & 0xFFFFFF;
        return new CLITablePtr(table, rowNo);
    }

    private static final byte[] MAP_ENCODED =  new byte[] {CLITableConstants.CLI_TABLE_TYPE_DEF, CLITableConstants.CLI_TABLE_TYPE_REF, CLITableConstants.CLI_TABLE_TYPE_SPEC };
    public static CLITablePtr fromTypeDefOrRefOrSpecEncoded(int typeDefOrRefOrSpecEncoded)
    {
        byte table = MAP_ENCODED[typeDefOrRefOrSpecEncoded & 2];
        int rowNo = typeDefOrRefOrSpecEncoded >> 2;
        return new CLITablePtr(table, rowNo);
    }
}
