package com.vztekoverflow.cil.parser.pe;

import com.vztekoverflow.cil.parser.CILParserException;
import com.vztekoverflow.cil.parser.ByteSequenceBuffer;
import com.vztekoverflow.cil.parser.ParserBundle;
import org.graalvm.polyglot.io.ByteSequence;

/**
 * Class representing a PE file containing a .NET assembly, as specified in II.25 File format extensions to PE.
 */
public class PEFile {

    private final PEFileHeader fileHeader;
    private final PEOptionalHeader optionalHeader;
    private final PEDataDirs dataDirs;
    private final PESectionHeaders sectionHeaders;

    public ByteSequence getBytes() {
        return bytes;
    }

    private final ByteSequence bytes;

    public PEFile(PEFileHeader fileHeader, PEOptionalHeader optionalHeader, PEDataDirs dataDirs, PESectionHeaders sectionHeaders, ByteSequence bytes) {
        this.fileHeader = fileHeader;
        this.optionalHeader = optionalHeader;
        this.dataDirs = dataDirs;
        this.sectionHeaders = sectionHeaders;
        this.bytes=bytes;
    }



    public static PEFile create(ByteSequence data)
    {
        if(data.byteAt(0) != 'M' || data.byteAt(1) != 'Z')
            throw new CILParserException(ParserBundle.message("cil.parser.exception.missing.mz"));

        ByteSequenceBuffer dataBuffer = new ByteSequenceBuffer(data);
        dataBuffer.setPosition(0x3c);

        int PEStart = dataBuffer.getInt();
        if(PEStart < 0 || PEStart > data.length())
        {
            throw new CILParserException(ParserBundle.message("cil.parser.exception.missing.mz"));
        }

        dataBuffer.setPosition(PEStart);

        if(dataBuffer.getByte() != 'P' || dataBuffer.getByte() != 'E' || dataBuffer.getByte() != 0 || dataBuffer.getByte() != 0)
            throw new CILParserException(ParserBundle.message("cil.parser.exception.missing.pe"));

        PEFileHeader fileHeader = PEFileHeader.read(dataBuffer);

        if(fileHeader.getSizeOfOptionalHeader() < 96)
            throw new CILParserException(ParserBundle.message("cil.parser.exception.missing.pe.header.small", fileHeader.getSizeOfOptionalHeader()));

        PEOptionalHeader optionalHeader = PEOptionalHeader.read(dataBuffer);

        if(optionalHeader.getNumberOfRvaAndSizes() < 16)
            throw new CILParserException(ParserBundle.message("cil.parser.exception.missing.pe.header.rva.small", optionalHeader.getNumberOfRvaAndSizes()));

        PEDataDirs dataDirs = PEDataDirs.read(dataBuffer, optionalHeader.getNumberOfRvaAndSizes());

        if(dataDirs.getSize(14) == 0)
            throw new CILParserException(ParserBundle.message("cil.parser.exception.cli.header.empty"));

        PESectionHeaders sectionHeaders = PESectionHeaders.read(dataBuffer, fileHeader.getNumberOfSections());

        return new PEFile(fileHeader, optionalHeader, dataDirs, sectionHeaders, data);
    }

    /**
     * Get the PE file offset for the specified RVA.
     */
    public int getFileOffsetForRVA(int RVA) {
        PESectionHeader section = sectionHeaders.getSectionForRVA(RVA);
        if (section == null)
        {
            return -1;
        }

        return section.getPointerToRawData() + RVA - section.getVirtualAddress();
    }

    /**
     * Get the PE file offset for the CLI Header.
     */
    public int getFileOffsetForCLIHeader()
    {
        return getFileOffsetForRVA(dataDirs.getRva(14));
    }



}
