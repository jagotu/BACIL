package com.vztekoverflow.bacil.parser.cli.tables;

import com.vztekoverflow.bacil.bytecode.Bytes;

import java.util.UUID;

/**
 * A pointer to the GUID heap, returning {@link UUID}.
 */
public class CLIGUIDHeapPtr extends CLIHeapPtr<UUID>{

    public CLIGUIDHeapPtr(int offset) {
        super(offset);
    }

    /**
     * Read a GUID from the GUID heap as an {@link UUID}.
     * @param heapData the bytes of the heap
     * @return the GUID
     */
    @Override
    public UUID read(byte[] heapData) {
        long leastSig = Bytes.getLong(heapData, offset);
        long mostSig = Bytes.getLong(heapData, offset+4);
        return new UUID(mostSig, leastSig);
    }


}
