package com.vztekoverflow.bacil.parser.cli.tables;

import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITableConstants;

/**
 * Class representing a generic pointer to a row in a CLI Metadata table.
 */
public class CLITablePtr {
    private final byte tableId;
    private final int rowNo;

    /**
     * Create a new table pointer.
     * @param tableId ID of the table this pointer points to
     * @param rowNo the row number this pointer points to
     */
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

    /**
     * Create a table pointer from a metadata token (III.1.9 Metadata tokens).
     * @param token the metadata token
     * @return a table pointer pointer equivalent to the specified token
     */
    public static CLITablePtr fromToken(int token)
    {
        byte table = (byte)(token >> 24);
        int rowNo = token & 0xFFFFFF;
        return new CLITablePtr(table, rowNo);
    }

    //A translation table for II.23.2.8 TypeDefOrRefOrSpecEncoded
    private static final byte[] MAP_ENCODED =  new byte[] {CLITableConstants.CLI_TABLE_TYPE_DEF, CLITableConstants.CLI_TABLE_TYPE_REF, CLITableConstants.CLI_TABLE_TYPE_SPEC };

    /**
     * Translate a TypeDefOrRefOrSpecEncoded value (as specified in II.23.2.8 TypeDefOrRefOrSpecEncoded) to
     * a table pointer.
     * @param typeDefOrRefOrSpecEncoded source value
     * @return a table pointer pointer equivalent to the specified TypeDefOrRefOrSpecEncoded value
     */
    public static CLITablePtr fromTypeDefOrRefOrSpecEncoded(int typeDefOrRefOrSpecEncoded)
    {
        byte table = MAP_ENCODED[typeDefOrRefOrSpecEncoded & 3];
        int rowNo = typeDefOrRefOrSpecEncoded >> 2;
        return new CLITablePtr(table, rowNo);
    }
}
