package com.vztekoverflow.cil.parser.cli.table;

import java.util.Iterator;

/**
 * An iterator of all {@link CLITableRow} rows in a table.
 * @param <T> the {@link CLITableRow} descendant for this specific table
 */
public class CLITableIterator<T extends CLITableRow<T>> implements Iterator<T> {
    private T current;

    public CLITableIterator(T current) {
        this.current = current;
    }

    @Override
    public boolean hasNext() {
        return current.hasNext();
    }

    @Override
    public T next() {
        T tmpCurrent = current;
        current = current.next();
        return tmpCurrent;
    }
}
