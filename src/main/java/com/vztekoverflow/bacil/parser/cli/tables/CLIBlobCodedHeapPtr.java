package com.vztekoverflow.bacil.parser.cli.tables;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.dsl.processor.java.compiler.Compiler;
import com.vztekoverflow.bacil.parser.CompressedInteger;
import com.vztekoverflow.bacil.parser.Positionable;

import java.util.Arrays;

public abstract class CLIBlobCodedHeapPtr extends CLIHeapPtr<byte[]> implements Positionable {

    public CLIBlobCodedHeapPtr(int offset) {
        super(offset);
    }

    private int position = offset;


    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public byte[] read(byte[] heapData) {
        int length = CompressedInteger.read(heapData, this);

        return Arrays.copyOfRange(heapData, position, position+length);

    }
}
