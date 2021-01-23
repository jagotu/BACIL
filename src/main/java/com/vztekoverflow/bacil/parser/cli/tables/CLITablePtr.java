package com.vztekoverflow.bacil.parser.cli.tables;

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
}
