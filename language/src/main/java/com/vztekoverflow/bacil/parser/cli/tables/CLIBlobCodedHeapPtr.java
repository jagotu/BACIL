package com.vztekoverflow.bacil.parser.cli.tables;

import com.vztekoverflow.bacil.parser.CompressedInteger;
import com.vztekoverflow.bacil.parser.Positionable;

import java.util.Arrays;

/**
 * Represents a pointer to a "blob-coded" heap, whose blobs are length prefixed as described in
 * II.24.2.4 #US and #Blob heaps.
 */
public abstract class CLIBlobCodedHeapPtr extends CLIHeapPtr<byte[]> implements Positionable {

    public CLIBlobCodedHeapPtr(int offset) {
        super(offset);
    }

    private int position = offset;


    /**
     * Get the current position in the heap data.
     */
    @Override
    public int getPosition() {
        return position;
    }

    /**
     * Set the current position in the heap data.
     */
    @Override
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Get the blob data as a byte[].
     * @param heapData the bytes of the heap
     * @return the data of the blob
     */
    @Override
    public byte[] read(byte[] heapData) {
        int length = CompressedInteger.read(heapData, this);

        return Arrays.copyOfRange(heapData, position, position+length);

    }
}
