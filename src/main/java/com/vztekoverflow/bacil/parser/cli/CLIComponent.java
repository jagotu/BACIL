package com.vztekoverflow.bacil.parser.cli;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.source.Source;
import com.vztekoverflow.bacil.BACILLanguage;
import com.vztekoverflow.bacil.parser.BACILParserException;
import com.vztekoverflow.bacil.parser.ByteSequenceBuffer;
import com.vztekoverflow.bacil.parser.cli.tables.CLITables;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablesHeader;
import com.vztekoverflow.bacil.parser.pe.PEFile;
import org.graalvm.polyglot.io.ByteSequence;

public class CLIComponent {

    private final CLIHeader cliHeader;
    private final CLIMetadata cliMetadata;

    public BACILLanguage getLanguage() {
        return language;
    }

    private final BACILLanguage language;

    @CompilationFinal(dimensions = 1)
    private final byte[] blobHeap;
    @CompilationFinal(dimensions = 1)
    private final byte[] stringHeap;
    @CompilationFinal(dimensions = 1)
    private final byte[] guidHeap;

    private final PEFile pe;

    public CLIHeader getCliHeader() {
        return cliHeader;
    }

    public CLIMetadata getCliMetadata() {
        return cliMetadata;
    }

    private final CLITables tables;

    public byte[] getBlobHeap() {
        return blobHeap;
    }

    public byte[] getStringHeap() {
        return stringHeap;
    }

    public byte[] getGuidHeap() {
        return guidHeap;
    }

    public CLITables getTables() {
        return tables;
    }

    public int getFileOffsetForRVA(int RVA) {
        return pe.getFileOffsetForRVA(RVA);
    }

    public ByteSequenceBuffer getBuffer(int RVA) {
        ByteSequenceBuffer buf = new ByteSequenceBuffer(pe.getBytes());
        buf.setPosition(pe.getFileOffsetForRVA(RVA));
        return buf;
    }

    public CLIComponent(CLIHeader cliHeader, CLIMetadata cliMetadata, byte[] blobHeap, byte[] stringHeap, byte[] guidHeap, CLITables tables, PEFile pe, BACILLanguage language) {
        this.cliHeader = cliHeader;
        this.cliMetadata = cliMetadata;
        this.blobHeap = blobHeap;
        this.stringHeap = stringHeap;
        this.guidHeap = guidHeap;
        this.tables = tables;
        this.pe = pe;
        this.language = language;
    }

    public static CLIComponent parseComponent(ByteSequence bytes, Source source, BACILLanguage language) {

        PEFile peFile = PEFile.create(bytes);

        final int cliHeaderOffset = peFile.getFileOffsetForCLIHeader();
        if (cliHeaderOffset < 0 || cliHeaderOffset > bytes.length())
            throw new BACILParserException("Unexpected offset of CLI header.");

        ByteSequenceBuffer dataBuffer = new ByteSequenceBuffer(bytes);
        dataBuffer.setPosition(cliHeaderOffset);

        CLIHeader cliHeader = CLIHeader.read(dataBuffer);

        if (cliHeader.getMetaData().getSize() <= 0)
            throw new BACILParserException("Unexpected empty CLI metadata.");

        final int cliMetadataOffset = peFile.getFileOffsetForRVA(cliHeader.getMetaData().getRva());

        if (cliMetadataOffset < 0 || cliMetadataOffset > bytes.length())
            throw new BACILParserException("Unexpected offset of CLI metadata.");

        dataBuffer.setPosition(cliMetadataOffset);

        CLIMetadata cliMetadata = CLIMetadata.read(dataBuffer);


        CLITables tables = new CLITables(cliMetadata.getStream("#~", bytes));

        final byte[] blobHeap = cliMetadata.getStream("#Blob", bytes).toByteArray();
        final byte[] stringHeap = cliMetadata.getStream("#Strings", bytes).toByteArray();
        final byte[] guidHeap = cliMetadata.getStream("#GUID", bytes).toByteArray();

        return new CLIComponent(cliHeader, cliMetadata, blobHeap, stringHeap, guidHeap, tables, peFile, language);
    }



}
