package com.vztekoverflow.bacil.parser.cli.tables;

public abstract class CLIHeapPtr<T> {

    protected final int offset;

    public CLIHeapPtr(int offset) {
        this.offset = offset;
    }

    public abstract T read(byte[] heapData);


}
