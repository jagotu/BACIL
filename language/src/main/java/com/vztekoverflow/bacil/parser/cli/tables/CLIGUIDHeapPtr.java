package com.vztekoverflow.bacil.parser.cli.tables;

import com.vztekoverflow.bacil.bytecode.Bytes;

import java.util.UUID;

public class CLIGUIDHeapPtr extends CLIHeapPtr<UUID>{

    public CLIGUIDHeapPtr(int offset) {
        super(offset);
    }

    @Override
    public UUID read(byte[] heapData) {
        long leastSig = Bytes.getLong(heapData, offset);
        long mostSig = Bytes.getLong(heapData, offset+4);
        return new UUID(mostSig, leastSig);
    }


}
