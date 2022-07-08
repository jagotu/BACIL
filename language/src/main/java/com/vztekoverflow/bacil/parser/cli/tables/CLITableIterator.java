package com.vztekoverflow.bacil.parser.cli.tables;

import java.util.Iterator;

/**
 * An iterator of all {@link CLITableRow} rows in a table.
 * @param <T> the {@link CLITableRow} descendant for this specific table
 */
public class CLITableIterator<T extends CLITableRow<T>> implements Iterator<T> {

    private boolean first = true;
    private T current;
    private final boolean empty;

    public CLITableIterator(T current, boolean empty) {
        this.current = current;
        this.empty = empty;
    }

    @Override
    public boolean hasNext() {
        if(first)
        {
            return !empty;
        }
        return current.hasNext();
    }

    @Override
    public T next() {
        if(first)
        {
            first = false;
            return current;
        }

        current = current.next();
        return current;
    }
}
