package com.vztekoverflow.bacil.parser.cli.tables;


import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.parser.BACILParserException;

import java.util.Iterator;

public abstract class CLITableRow<T extends CLITableRow<T>> implements Iterable<T> {

    protected final CLITables tables;
    protected final int cursor;
    private final int rowIndex;


    public abstract int getLength();
    public abstract byte getTableId();
    protected abstract T createNew(CLITables tables, int cursor, int rowIndex);

    public CLITableRow(CLITables tables, int cursor, int rowIndex) {
        this.tables = tables;
        this.cursor = cursor;
        this.rowIndex = rowIndex;
    }

    public final boolean hasNext() {
        return rowIndex < tables.getTablesHeader().getRowCount(getTableId());
    }

    public final T next()
    {
        return skip(1);
    };

    public final T skip(int count)
    {
        return createNew(tables, cursor+count*getLength(), rowIndex+count);
    }

    public final T skip(CLITablePtr ptr) {
        if (getTableId() != ptr.getTableId())
        {
            throw new BACILInternalError(String.format("Wrongly typed ptr used to index into table: used %d, expected %d",
                    ptr.getTableId(), getTableId()));
        }

        return skip(ptr.getRowNo()-1);

    }


    public final int getCursor() {
        return cursor;
    }

    protected final byte getByte(int offset)
    {
        return tables.getTablesData()[cursor+offset];
    }

    protected final short getShort(int offset)
    {
        final byte[] tableData = tables.getTablesData();
        short result = (short)(tableData[cursor+offset] & 0xff);
        result |= (tableData[cursor+offset+1] & 0xff) << 8;
        return result;
    }

    protected final int getInt(int offset)
    {
        final byte[] tableData = tables.getTablesData();
        int result = (tableData[cursor+offset] & 0xff);
        result |= (tableData[cursor+offset+1] & 0xff) << 8;
        result |= (tableData[cursor+offset+2] & 0xff) << 16;
        result |= (tableData[cursor+offset+3] & 0xff) << 24;
        return result;
    }

    protected final long getLong(int offset)
    {
        final byte[] tableData = tables.getTablesData();
        long result = (tableData[cursor+offset] & 0xff);
        result |= (tableData[cursor+offset+1] & 0xff) << 8;
        result |= (tableData[cursor+offset+2] & 0xff) << 16;
        result |= ((long)tableData[cursor+offset+3] & 0xff) << 24;
        result |= ((long)tableData[cursor+offset+4] & 0xff) << 32;
        result |= ((long)tableData[cursor+offset+5] & 0xff) << 40;
        result |= ((long)tableData[cursor+offset+6] & 0xff) << 48;
        result |= ((long)tableData[cursor+offset+7] & 0xff) << 56;
        return result;
    }

    @Override
    public Iterator<T> iterator() {
        return new CLITableIterator<T>(createNew(tables, cursor, rowIndex));
    }
}
