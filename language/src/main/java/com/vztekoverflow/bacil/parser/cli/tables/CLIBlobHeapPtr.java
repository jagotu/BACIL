package com.vztekoverflow.bacil.parser.cli.tables;

/**
 * A pointer to the Blob heap, returning a {@link byte[]}.
 */
public class CLIBlobHeapPtr extends CLIBlobCodedHeapPtr {

    public CLIBlobHeapPtr(int offset) {
        super(offset);
    }

}
