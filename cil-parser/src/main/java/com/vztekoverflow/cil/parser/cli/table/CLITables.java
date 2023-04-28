package com.vztekoverflow.cil.parser.cli.table;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.vztekoverflow.cil.parser.ByteSequenceBuffer;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITableHeads;
import org.graalvm.polyglot.io.ByteSequence;

/**
 * Class storing the raw CLI Metadata tables data and its metadata, as described in
 * II.24.2.6 #~ stream.
 */
public class CLITables {

    private final CLITablesHeader tablesHeader;

    @CompilationFinal(dimensions = 1)
    private final byte[] tablesData;

    private final boolean stringHeapBig;
    private final boolean GUIDHeapBig;
    private final boolean BlobHeapBig;
    private final CLITableHeads tableHeads;

    /**
     * Parse CLI Metadata tables information from the #~ stream.
     * @param tableStream a {@link ByteSequence} representing the #~ stream.
     */
    public CLITables(ByteSequence tableStream) {
        ByteSequenceBuffer buffer = new ByteSequenceBuffer(tableStream);
        tablesHeader = CLITablesHeader.read(buffer);
        tablesData = tableStream.subSequence(buffer.getPosition(), tableStream.length()-1).toByteArray();

        stringHeapBig = (tablesHeader.getHeapSizes() & 1) != 0;
        GUIDHeapBig = (tablesHeader.getHeapSizes() & 2) != 0;
        BlobHeapBig = (tablesHeader.getHeapSizes() & 4) != 0;
        tableHeads = new CLITableHeads(this);
    }

    public CLITablesHeader getTablesHeader() {
        return tablesHeader;
    }

    public byte[] getTablesData() {
        return tablesData;
    }

    public boolean isStringHeapBig()
    {
        return stringHeapBig;
    }

    public boolean isGUIDHeapBig()
    {
        return GUIDHeapBig;
    }

    public boolean isBlobHeapBig()
    {
        return BlobHeapBig;
    }

    public CLITableHeads getTableHeads() {
        return tableHeads;
    }
}
