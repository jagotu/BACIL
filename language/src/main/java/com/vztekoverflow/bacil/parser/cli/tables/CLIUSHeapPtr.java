package com.vztekoverflow.bacil.parser.cli.tables;

import java.nio.charset.StandardCharsets;

public class CLIUSHeapPtr extends CLIBlobCodedHeapPtr {
    public CLIUSHeapPtr(int offset) {
        super(offset);
    }

    public String readString(byte[] heapData)
    {
        byte[] str = read(heapData);
        return new String(str, 0, str.length-1, StandardCharsets.UTF_16LE);
    }
}
