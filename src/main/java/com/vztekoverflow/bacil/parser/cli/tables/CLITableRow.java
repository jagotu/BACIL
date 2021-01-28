package com.vztekoverflow.bacil.parser.cli.tables;


import com.oracle.truffle.api.CompilerAsserts;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.bytecode.Bytes;
import com.vztekoverflow.bacil.parser.BACILParserException;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITableConstants;

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
            CompilerAsserts.neverPartOfCompilation();
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
        final byte[] tableData = tables.getTablesData();
        return tableData[cursor+offset];
    }

    protected final short getShort(int offset)
    {
        final byte[] tableData = tables.getTablesData();
        return Bytes.getShort(tableData, cursor+offset);
    }

    protected final int getInt(int offset)
    {
        final byte[] tableData = tables.getTablesData();
        return Bytes.getInt(tableData, cursor+offset);
    }

    protected final long getLong(int offset)
    {
        final byte[] tableData = tables.getTablesData();
        return Bytes.getLong(tableData, cursor+offset);
    }

    public final CLITablePtr getPtr()
    {
        return new CLITablePtr(this.getTableId(), rowIndex+1);
    }

    public int getRowNo() {
        return rowIndex+1;
    }

    @Override
    public Iterator<T> iterator() {
        return new CLITableIterator<T>(createNew(tables, cursor, rowIndex));
    }

    protected final boolean areSmallEnough(byte... tables)
    {
        final int limit;
        if(tables.length <= 1)
        {
            limit = 65536;
        } else if (tables.length <= 2) {
            limit = 65536 >> 1;
        } else if (tables.length <= 4) {
            limit = 65536 >> 2;
        } else if (tables.length <= 8) {
            limit = 65536 >> 3;
        } else if (tables.length <= 16) {
            limit = 65536 >> 4;
        } else {
            limit = 65536 >> 5;
        }
        for (byte table : tables)
        {
            if(table != CLITableConstants.CLI_TABLE_NONE_ID && this.tables.getTablesHeader().getRowCount(table) >= limit)
                return false;
        }
        return true;
    }
}
