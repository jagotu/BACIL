package com.vztekoverflow.bacil.parser.cli.tables;

import java.util.Iterator;

public class CLITableIterator<T extends CLITableRow<T>> implements Iterator<T> {

    private boolean first = true;
    private T current;

    public CLITableIterator(T current) {
        this.current = current;
    }

    @Override
    public boolean hasNext() {
        if(first)
        {
            return true;
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
