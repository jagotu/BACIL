package com.vztekoverflow.bacil.parser.cli;

import com.vztekoverflow.bacil.parser.BACILParserException;
import com.vztekoverflow.bacil.parser.ByteSequenceBuffer;
import com.vztekoverflow.bacil.parser.RvaSizePair;

public class CLIHeader {
    private final short majorRuntimeVersion;
    private final short minorRuntimeVersion;
    private final RvaSizePair metaData;
    private final int flags;
    private final int entryPointToken;
    private final RvaSizePair resources;
    private final RvaSizePair strongNameSignature;
    private final RvaSizePair codeManagerTable;
    private final RvaSizePair vTableFixups;
    private final RvaSizePair exportAddressTableJumps;
    private final RvaSizePair managedNativeHeader;

    public CLIHeader(short majorRuntimeVersion, short minorRuntimeVersion, RvaSizePair metaData, int flags, int entryPointToken, RvaSizePair resources, RvaSizePair strongNameSignature, RvaSizePair codeManagerTable, RvaSizePair vTableFixups, RvaSizePair exportAddressTableJumps, RvaSizePair managedNativeHeader) {
        this.majorRuntimeVersion = majorRuntimeVersion;
        this.minorRuntimeVersion = minorRuntimeVersion;
        this.metaData = metaData;
        this.flags = flags;
        this.entryPointToken = entryPointToken;
        this.resources = resources;
        this.strongNameSignature = strongNameSignature;
        this.codeManagerTable = codeManagerTable;
        this.vTableFixups = vTableFixups;
        this.exportAddressTableJumps = exportAddressTableJumps;
        this.managedNativeHeader = managedNativeHeader;
    }

    public short getMajorRuntimeVersion() {
        return majorRuntimeVersion;
    }

    public short getMinorRuntimeVersion() {
        return minorRuntimeVersion;
    }

    public RvaSizePair getMetaData() {
        return metaData;
    }

    public int getFlags() {
        return flags;
    }

    public int getEntryPointToken() {
        return entryPointToken;
    }

    public RvaSizePair getResources() {
        return resources;
    }

    public RvaSizePair getStrongNameSignature() {
        return strongNameSignature;
    }

    public RvaSizePair getCodeManagerTable() {
        return codeManagerTable;
    }

    public RvaSizePair getvTableFixups() {
        return vTableFixups;
    }

    public RvaSizePair getExportAddressTableJumps() {
        return exportAddressTableJumps;
    }

    public RvaSizePair getManagedNativeHeader() {
        return managedNativeHeader;
    }

    public static CLIHeader read(ByteSequenceBuffer buf)
    {
        int cb = buf.getInt();
        if(cb < 72)
            throw new BACILParserException("CLI Header too small - is "  + buf.getInt() + ", expected at least 72");

        final short majorRuntimeVersion = buf.getShort();
        final short minorRuntimeVersion = buf.getShort();
        final RvaSizePair metaData = RvaSizePair.read(buf);
        final int flags = buf.getInt();
        final int entryPointToken = buf.getInt();
        final RvaSizePair resources = RvaSizePair.read(buf);
        final RvaSizePair strongNameSignature = RvaSizePair.read(buf);
        final RvaSizePair codeManagerTable = RvaSizePair.read(buf);
        final RvaSizePair vTableFixups = RvaSizePair.read(buf);
        final RvaSizePair exportAddressTableJumps = RvaSizePair.read(buf);
        final RvaSizePair managedNativeHeader = RvaSizePair.read(buf);

        return new CLIHeader(majorRuntimeVersion, minorRuntimeVersion, metaData, flags, entryPointToken, resources, strongNameSignature, codeManagerTable, vTableFixups, exportAddressTableJumps, managedNativeHeader);

    }
}
