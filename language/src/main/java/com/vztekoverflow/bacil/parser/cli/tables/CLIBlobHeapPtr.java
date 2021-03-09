package com.vztekoverflow.bacil.parser.cli.tables;

public class CLIBlobHeapPtr extends CLIBlobCodedHeapPtr {

    public CLIBlobHeapPtr(int offset) {
        super(offset);
    }

    public int getOffset()
    {
        return offset;
    }
}
