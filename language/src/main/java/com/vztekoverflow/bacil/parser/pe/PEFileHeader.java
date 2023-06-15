package com.vztekoverflow.bacil.parser.pe;

import com.vztekoverflow.bacil.parser.ByteSequenceBuffer;

/** Class representing a PE file header, as specified in II.25.2.2 PE file header. */
public class PEFileHeader {

  private final short machine;
  private final short numberOfSections;
  private final int timeDateStamp;
  private final int pointerToSymbolTable;
  private final int numberOfSymbols;
  private final short sizeOfOptionalHeader;
  private final short characteristics;

  public PEFileHeader(
      short machine,
      short numberOfSections,
      int timeDateStamp,
      int pointerToSymbolTable,
      int numberOfSymbols,
      short sizeOfOptionalHeader,
      short characteristics) {
    this.machine = machine;
    this.numberOfSections = numberOfSections;
    this.timeDateStamp = timeDateStamp;
    this.pointerToSymbolTable = pointerToSymbolTable;
    this.numberOfSymbols = numberOfSymbols;
    this.sizeOfOptionalHeader = sizeOfOptionalHeader;
    this.characteristics = characteristics;
  }

  public static PEFileHeader read(ByteSequenceBuffer buf) {
    final short machine = buf.getShort();
    final short numberOfSections = buf.getShort();
    final int timeDateStamp = buf.getInt();
    final int pointerToSymbolTable = buf.getInt();
    final int numberOfSymbols = buf.getInt();
    final short sizeOfOptionalHeader = buf.getShort();
    final short characteristics = buf.getShort();
    return new PEFileHeader(
        machine,
        numberOfSections,
        timeDateStamp,
        pointerToSymbolTable,
        numberOfSymbols,
        sizeOfOptionalHeader,
        characteristics);
  }

  public short getMachine() {
    return machine;
  }

  public short getNumberOfSections() {
    return numberOfSections;
  }

  public int getTimeDateStamp() {
    return timeDateStamp;
  }

  public int getPointerToSymbolTable() {
    return pointerToSymbolTable;
  }

  public int getNumberOfSymbols() {
    return numberOfSymbols;
  }

  public short getSizeOfOptionalHeader() {
    return sizeOfOptionalHeader;
  }

  public short getCharacteristics() {
    return characteristics;
  }
}
