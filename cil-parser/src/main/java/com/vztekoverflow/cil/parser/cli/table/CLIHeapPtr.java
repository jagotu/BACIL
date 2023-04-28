package com.vztekoverflow.cil.parser.cli.table;

/**
 * Represents a pointer to a heap, returning typed results.
 * @param <T> The type of the data read from the heap.
 */
public abstract class CLIHeapPtr<T> {

    protected final int offset;

    /**
     * Create a heap pointer for the given offset.
     */
    public CLIHeapPtr(int offset) {
        this.offset = offset;
    }

    /**
     * Read the value from the heap.
     * @param heapData the bytes of the heap
     * @return the read value
     */
    public abstract T read(byte[] heapData);


}
