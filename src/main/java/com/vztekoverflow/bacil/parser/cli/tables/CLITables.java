package com.vztekoverflow.bacil.parser.cli.tables;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.vztekoverflow.bacil.parser.ByteSequenceBuffer;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITableHeads;
import org.graalvm.polyglot.io.ByteSequence;

public class CLITables {

    private final CLITablesHeader tablesHeader;

    @CompilationFinal(dimensions = 1)
    private final byte[] tablesData;

    private final boolean stringHeapBig;
    private final boolean GUIDHeapBig;
    private final boolean BlobHeapBig;
    private final CLITableHeads tableHeads;

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
