package com.vztekoverflow.cil.parser.pe;

import com.vztekoverflow.cil.parser.ByteSequenceBuffer;

/**
 * Class representing a single PE Section header, as specified in II.25.3 Section headers.
 */
public class PESectionHeader {
    private final long name;
    private final int virtualSize;
    private final int virtualAddress;
    private final int sizeOfRawData;
    private final int pointerToRawData;
    private final int pointerToRelocations;
    private final int pointerToLinenumbers;
    private final short numberOfRelocations;
    private final short numberOfLinenumbers;
    private final int characteristics;

    public PESectionHeader(long name, int virtualSize, int virtualAddress, int sizeOfRawData, int pointerToRawData, int pointerToRelocations, int pointerToLinenumbers, short numberOfRelocations, short numberOfLinenumbers, int characteristics) {
        this.name = name;
        this.virtualSize = virtualSize;
        this.virtualAddress = virtualAddress;
        this.sizeOfRawData = sizeOfRawData;
        this.pointerToRawData = pointerToRawData;
        this.pointerToRelocations = pointerToRelocations;
        this.pointerToLinenumbers = pointerToLinenumbers;
        this.numberOfRelocations = numberOfRelocations;
        this.numberOfLinenumbers = numberOfLinenumbers;
        this.characteristics = characteristics;
    }

    public long getName() {
        return name;
    }

    public int getVirtualSize() {
        return virtualSize;
    }

    public int getVirtualAddress() {
        return virtualAddress;
    }

    public int getSizeOfRawData() {
        return sizeOfRawData;
    }

    public int getPointerToRawData() {
        return pointerToRawData;
    }

    public int getPointerToRelocations() {
        return pointerToRelocations;
    }

    public int getPointerToLinenumbers() {
        return pointerToLinenumbers;
    }

    public short getNumberOfRelocations() {
        return numberOfRelocations;
    }

    public short getNumberOfLinenumbers() {
        return numberOfLinenumbers;
    }

    public int getCharacteristics() {
        return characteristics;
    }

    public static PESectionHeader read(ByteSequenceBuffer buf)
    {
        final long name = buf.getLong();
        final int virtualSize = buf.getInt();
        final int virtualAddress = buf.getInt();
        final int sizeOfRawData = buf.getInt();
        final int pointerToRawData = buf.getInt();
        final int pointerToRelocations = buf.getInt();
        final int pointerToLinenumbers = buf.getInt();
        final short numberOfRelocations = buf.getShort();
        final short numberOfLinenumbers = buf.getShort();
        final int characteristics = buf.getInt();

        return new PESectionHeader(name, virtualSize, virtualAddress, sizeOfRawData, pointerToRawData, pointerToRelocations, pointerToLinenumbers, numberOfRelocations, numberOfLinenumbers, characteristics);
    }
}
