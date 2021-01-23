package com.vztekoverflow.bacil.parser.cli.tables;

import java.util.Iterator;

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
        T tmp = current;
        current = current.next();
        return tmp;
    }
}
