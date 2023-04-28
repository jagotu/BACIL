package com.vztekoverflow.cil.parser.cli.table;


import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.vztekoverflow.cil.parser.CILParserException;
import com.vztekoverflow.cil.parser.ParserBundle;
import com.vztekoverflow.cil.parser.bytecode.Bytes;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITableConstants;

import java.util.Iterator;

/**
 * Class representing a row in a CLI Metadata table.
 * @param <T> the concrete type for the specific table
 */
public abstract class CLITableRow<T extends CLITableRow<T>> implements Iterable<T> {

    protected final CLITables tables; //holds the actual table data
    protected final int cursor; //byte offset where this row starts
    private final int rowIndex; //index of the row in the table

    /*
    When table data was being retrieved from tables.GetTableData() instead of stored directly,
    the CompilationFinal flag was getting lost when the TableRow was virtualized during escape analysis --
    tables.GetTableData() would be a constant but tables.GetTableData()[0] would not. Storing a pointer
    to the data ourselves and re-marking it as a CompilationFinal array seems to solve this issue.
     */
    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private final byte[] tableData;


    /**
     * Get the length of one row in bytes.
     */
    public abstract int getLength();

    /**
     * Get the table id for this row.
     */
    public abstract byte getTableId();

    /**
     * Create a properly typed new {@link CLITableRow} pointing to the specified row at a specified byte offset.
     * @param tables {@link CLITables} holding the table data
     * @param cursor byte offset of the row
     * @param rowIndex index of the row
     * @return {@link CLITableRow} pointing to the specified row
     */
    protected abstract T createNew(CLITables tables, int cursor, int rowIndex);

    /**
     * Create a new {@link CLITableRow} pointing to the specified row at a specified byte offset.
     * @param tables {@link CLITables} holding the table data
     * @param cursor byte offset of the row
     * @param rowIndex index of the row
     */
    public CLITableRow(CLITables tables, int cursor, int rowIndex) {
        this.tables = tables;
        this.cursor = cursor;
        this.rowIndex = rowIndex;
        this.tableData = tables.getTablesData();
    }

    /**
     * Check whether there's any row in the table after the current one.
     */
    public final boolean hasNext() {
        return rowIndex < tables.getTablesHeader().getRowCount(getTableId())-1;
    }

    /**
     * Get the next table row (properly typed).
     */
    public final T next()
    {
        return skip(1);
    };

    /**
     * Get a (properly typed) pointer to a row that's the specified amount of rows after the current one.
     * @param count amount of rows to skip
     * @return a pointer to the row that's {@code count} rows after the current one
     */

    public final T skip(int count)
    {
        return createNew(tables, cursor+count*getLength(), rowIndex+count);
    }

    /**
     * Get a (properly typed) pointer to a row that's the amount of rows specified in the pointer after the current one.
     * Also checks that the {@link CLITablePtr} points to the correct table.
     *
     * This operation mainly makes sense when the current row is the first row, therefore "resolving" the CLITablePtr.
     * @param ptr a {@link CLITablePtr} which is used to skip
     * @return a pointer to the row that's {@code ptr.rowNo} rows after the current one
     */
    public final T skip(CLITablePtr ptr) {
        if (getTableId() != ptr.getTableId())
        {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new CILParserException(ParserBundle.message("cil.parser.exception.ptr.wronglyTyped", ptr.getTableId(), getTableId()));
        }

        return skip(ptr.getRowNo()-1);

    }

    /**
     * Get a byte at the specified offset of current row.
     */
    protected final byte getByte(int offset)
    {
        return tableData[cursor+offset];
    }

    /**
     * Get a short at the specified offset of current row.
     */
    protected final short getShort(int offset)
    {
        return Bytes.getShort(tableData, cursor+offset);
    }

    /**
     * Get an int at the specified offset of current row.
     */
    protected final int getInt(int offset)
    {
        return Bytes.getInt(tableData, cursor+offset);
    }

    /**
     * Get an unsigned short at the specified offset of current row.
     * Returns an int cause Java doesn't support unsigned shorts.
     */
    protected final int getUShort(int offset)
    {
        return Bytes.getUShort(tableData, cursor+offset);
    }

    /**
     * Get an unsigned int at the specified offset of current row.
     * Returns a long cause Java doesn't support unsigned ints.
     */
    protected final long getUInt(int offset)
    {
        return Bytes.getUInt(tableData, cursor+offset);
    }

    /**
     * Get a long at the specified offset of current row.
     */
    protected final long getLong(int offset)
    {
        return Bytes.getLong(tableData, cursor+offset);
    }

    /**
     * Get a {@link CLITablePtr} pointing to the current row.
     */
    public final CLITablePtr getPtr()
    {
        return new CLITablePtr(this.getTableId(), rowIndex+1);
    }

    /**
     * Get the current row number.
     */
    public int getRowNo() {
        return rowIndex+1;
    }

    /**
     * Get an iterator for all rows in this table.
     */
    @Override
    public Iterator<T> iterator() {
        return new CLITableIterator<T>(createNew(tables, cursor, rowIndex));
    }

    /**
     * Checks whether the specified tables are small enough to use a 2 byte coded index.
     * Coded indices are described II.24.2.6 #~ stream:
     *
     * If e is a coded index that points into table ti out of n possible tables t0,...,tn-1, then it
     * is stored as e &lt;&lt; (log n) | tag {t0,...,tn-1}[ti] using 2 bytes if the maximum number
     * of rows of tables t0,...,tn-1, is less than 2^(16 – (log n)), and using 4 bytes otherwise.
     * @param tables the tables this coded index can point to
     * @return whether a 2 byte coded index should be used
     */
    @ExplodeLoop
    protected final boolean areSmallEnough(byte[] tables)
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
