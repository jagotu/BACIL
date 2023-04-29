package com.vztekoverflow.cil.parser.cli;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.vztekoverflow.cil.parser.ByteSequenceBuffer;
import com.vztekoverflow.cil.parser.CILParserException;
import com.vztekoverflow.cil.parser.ParserBundle;
import com.vztekoverflow.cil.parser.cli.table.CLITables;
import com.vztekoverflow.cil.parser.cli.table.CLITablesHeader;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITableConstants;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITableHeads;
import com.vztekoverflow.cil.parser.pe.PEFile;
import org.graalvm.polyglot.io.ByteSequence;

/**
 * A class representing a CLI Component, as described in I.9.1 Components and assemblies.
 */
public class CLIFile {

    private final CLIHeader cliHeader;
    private final CLIMetadata cliMetadata;
    private final AssemblyIdentity assemblyIdentity;

    @CompilationFinal(dimensions = 1)
    private final byte[] blobHeap;
    @CompilationFinal(dimensions = 1)
    private final byte[] stringHeap;
    @CompilationFinal(dimensions = 1)
    private final byte[] guidHeap;
    @CompilationFinal(dimensions = 1)
    private final byte[] USHeap;

    private final PEFile pe;

    /**
     * Get a {@link ByteSequenceBuffer} representing the data of this component starting at the specified RVA.
     * @param RVA the RVA position to start at
     * @return a {@link ByteSequenceBuffer} starting at the specified RVA
     */
    public ByteSequenceBuffer getBuffer(int RVA) {
        ByteSequenceBuffer buf = new ByteSequenceBuffer(pe.getBytes());
        buf.setPosition(pe.getFileOffsetForRVA(RVA));
        return buf;
    }

    public CLIFile(CLIHeader cliHeader, CLIMetadata cliMetadata, byte[] blobHeap, byte[] stringHeap, byte[] guidHeap, byte[] USHeap, CLITables tables, PEFile pe) {
        this.cliHeader = cliHeader;
        this.cliMetadata = cliMetadata;
        this.blobHeap = blobHeap;
        this.stringHeap = stringHeap;
        this.guidHeap = guidHeap;
        this.USHeap = USHeap;
        this.tables = tables;
        this.pe = pe;

        final int assemblyDefCount = tables.getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_ASSEMBLY);
        if(assemblyDefCount > 1)
        {
            throw new CILParserException(ParserBundle.message("cli.parser.exception.file.moreAssemblies"));
        } else if (assemblyDefCount == 1)
        {
            assemblyIdentity = AssemblyIdentity.fromAssemblyRow(stringHeap, tables.getTableHeads().getAssemblyTableHead());
        } else {
            assemblyIdentity = null;
        }

    }

    /**
     * Parse component metadata from a PE file.
     * @param bytes a {@link ByteSequence} representing the PE bytes
     * @return a {@link CLIFile} representation of the component
     */
    public static CLIFile parse(ByteSequence bytes) {
        //Noone should load an assembly in compiled code.
        CompilerAsserts.neverPartOfCompilation();

        PEFile peFile = PEFile.create(bytes);

        final int cliHeaderOffset = peFile.getFileOffsetForCLIHeader();
        if (cliHeaderOffset < 0 || cliHeaderOffset > bytes.length())
            throw new CILParserException(ParserBundle.message("cli.parser.exception.header.offset"));

        ByteSequenceBuffer dataBuffer = new ByteSequenceBuffer(bytes);
        dataBuffer.setPosition(cliHeaderOffset);

        //Read CLI Header (II.25.3.3 CLI header)
        CLIHeader cliHeader = CLIHeader.read(dataBuffer);

        if (cliHeader.getMetaData().getSize() <= 0)
            throw new CILParserException(ParserBundle.message("cli.parser.exception.cli.metadata.empty"));

        final int cliMetadataOffset = peFile.getFileOffsetForRVA(cliHeader.getMetaData().getRva());

        if (cliMetadataOffset < 0 || cliMetadataOffset > bytes.length())
            throw new CILParserException(ParserBundle.message("cli.parser.exception.cli.metadata.offset"));

        dataBuffer.setPosition(cliMetadataOffset);

        //Read CLI Metadata (II.24 Metadata physical layout)
        CLIMetadata cliMetadata = CLIMetadata.read(dataBuffer);

        //Create table pointers (II.22 Metadata logical format: tables)
        CLITables tables = new CLITables(cliMetadata.getStream("#~", bytes));

        final byte[] blobHeap = cliMetadata.getStreamBytes("#Blob", bytes);
        final byte[] stringHeap = cliMetadata.getStreamBytes("#Strings", bytes);
        final byte[] guidHeap = cliMetadata.getStreamBytes("#GUID", bytes);
        final byte[] USHeap = cliMetadata.getStreamBytes("#US", bytes);


        return new CLIFile(cliHeader, cliMetadata, blobHeap, stringHeap, guidHeap, USHeap, tables, peFile);
    }

    public CLITableHeads getTableHeads()
    {
        return tables.getTableHeads();
    }

    public CLITablesHeader getTablesHeader()
    {
        return tables.getTablesHeader();
    }

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

    public byte[] getUSHeap() {
        return USHeap;
    }

    public AssemblyIdentity getAssemblyIdentity() {
        return assemblyIdentity;
    }

    @Override
    public String toString() {
        return assemblyIdentity.getName();
    }
}
