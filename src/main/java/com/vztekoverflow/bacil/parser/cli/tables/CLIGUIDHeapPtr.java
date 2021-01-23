package com.vztekoverflow.bacil.parser.cli.tables;

import java.util.UUID;

public class CLIGUIDHeapPtr extends CLIHeapPtr<UUID>{

    public CLIGUIDHeapPtr(int offset) {
        super(offset);
    }

    @Override
    public UUID read(byte[] heapData) {
        long leastSig = getLong(heapData, offset);
        long mostSig = getLong(heapData, offset+4);
        return new UUID(mostSig, leastSig);
    }

    private long getLong(byte[] heapData, int offset)
    {
        long result = (heapData[offset] & 0xff);
        result |= (heapData[offset+1] & 0xff) << 8;
        result |= (heapData[offset+2] & 0xff) << 16;
        result |= ((long)heapData[offset+3] & 0xff) << 24;
        result |= ((long)heapData[offset+4] & 0xff) << 32;
        result |= ((long)heapData[offset+5] & 0xff) << 40;
        result |= ((long)heapData[offset+6] & 0xff) << 48;
        result |= ((long)heapData[offset+7] & 0xff) << 56;
        return result;
    }
}
