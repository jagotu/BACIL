package com.vztekoverflow.bacil.parser.cli.tables;

import com.vztekoverflow.bacil.parser.BACILParserException;
import com.vztekoverflow.bacil.parser.ByteSequenceBuffer;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITableConstants;

public class CLITablesHeader {

    private final byte majorVersion;
    private final byte minorVersion;
    private final byte heapSizes;
    private final long valid;
    private final long sorted;
    private final int[] rowCounts;

    public CLITablesHeader(byte majorVersion, byte minorVersion, byte heapSizes, long valid, long sorted, int[] rowCounts) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.heapSizes = heapSizes;
        this.valid = valid;
        this.sorted = sorted;
        this.rowCounts = rowCounts;
    }

    public byte getMajorVersion() {
        return majorVersion;
    }

    public byte getMinorVersion() {
        return minorVersion;
    }

    public byte getHeapSizes() {
        return heapSizes;
    }

    public long getValid() {
        return valid;
    }

    public long getSorted() {
        return sorted;
    }

    public static CLITablesHeader read(ByteSequenceBuffer buf)
    {
        buf.getInt(); //Reserved
        final byte majorVersion = buf.getByte();
        final byte minorVersion = buf.getByte();
        final byte heapSizes = buf.getByte();
        buf.getByte(); //Reserved
        final long valid = buf.getLong();
        final long sorted = buf.getLong();

        final int[] rowCounts = new int[CLITableConstants.CLI_TABLE_MAX_ID+1];

        for(int i = 0;i<=CLITableConstants.CLI_TABLE_MAX_ID;i++)
        {
            if((valid & (1L << i)) != 0)
            {
                rowCounts[i] = buf.getInt();
                if(rowCounts[i] > 65535)
                {
                    //non-conforming SIMPLIFICATION
                    throw new BACILParserException("CIL files with a table larger than 65535 rows are not supported!");
                }
            }
        }

        return new CLITablesHeader(majorVersion, minorVersion, heapSizes, valid, sorted, rowCounts);
    }

    public final int getRowCount(byte table)
    {
        return rowCounts[table];
    }
}
