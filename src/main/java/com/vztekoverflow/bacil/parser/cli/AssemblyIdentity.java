package com.vztekoverflow.bacil.parser.cli;

import com.vztekoverflow.bacil.parser.cli.tables.generated.CLIAssemblyRefTableRow;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLIAssemblyTableRow;

public final class AssemblyIdentity {
    private final short majorVersion;
    private final short minorVersion;
    private final short buildNumber;
    private final short revisionNumber;
    private final String name;

    public AssemblyIdentity(short majorVersion, short minorVersion, short buildNumber, short revisionNumber, String name) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.buildNumber = buildNumber;
        this.revisionNumber = revisionNumber;
        this.name = name;
    }

    public static AssemblyIdentity fromAssemblyRow(byte[] stringHeap, CLIAssemblyTableRow row)
    {
        return new AssemblyIdentity(row.getMajorVersion(), row.getMinorVersion(), row.getBuildNumber(), row.getRevisionNumber(), row.getName().read(stringHeap));
    }

    public static AssemblyIdentity fromAssemblyRefRow(byte[] stringHeap, CLIAssemblyRefTableRow row)
    {
        return new AssemblyIdentity(row.getMajorVersion(), row.getMinorVersion(), row.getBuildNumber(), row.getRevisionNumber(), row.getName().read(stringHeap));
    }

    public boolean resolvesRef(AssemblyIdentity reference)
    {
        if(!name.equals(reference.name))
            return false;

        return true; // Ignore version numbers for now


        /*if(reference.majorVersion != -1)
        {
            if(reference.majorVersion != majorVersion)
                return false;

            if(reference.minorVersion != -1)
            {
                if(reference.minorVersion != minorVersion)
                    return false;

                if(reference.buildNumber != -1)
                {
                    if(reference.buildNumber != buildNumber)
                        return false;

                    if(reference.revisionNumber != -1)
                    {
                        if(reference.revisionNumber != revisionNumber)
                            return false;
                    }
                }
            }
        }

        return true;*/
    }

    public short getMajorVersion() {
        return majorVersion;
    }

    public short getMinorVersion() {
        return minorVersion;
    }

    public short getBuildNumber() {
        return buildNumber;
    }

    public short getRevisionNumber() {
        return revisionNumber;
    }

    public String getName() {
        return name;
    }
}
