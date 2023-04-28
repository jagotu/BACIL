package com.vztekoverflow.cil.parser.cli;

import com.vztekoverflow.cil.parser.cli.table.generated.CLIAssemblyRefTableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.CLIAssemblyTableRow;

/**
 * Representation of an assembly's identity, including its name and version.
 */
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

    /**
     * Get a representation of an assembly identity as defined in the Assembly table.
     * @param stringHeap string heap of the component from which the name will be read
     * @param row the {@link CLIAssemblyTableRow} defining the assembly
     * @return an {@link AssemblyIdentity} representing the assembly table row
     */
    public static AssemblyIdentity fromAssemblyRow(byte[] stringHeap, CLIAssemblyTableRow row)
    {
        return new AssemblyIdentity(row.getMajorVersion(), row.getMinorVersion(), row.getBuildNumber(), row.getRevisionNumber(), row.getName().read(stringHeap));
    }

    /**
     * Get a representation of an assembly identity as referenced in the AssemblyRef table.
     * @param stringHeap string heap of the component from which the name will be read
     * @param row the {@link CLIAssemblyRefTableRow} defining the assembly reference
     * @return an {@link AssemblyIdentity} representing the assembly reference
     */
    public static AssemblyIdentity fromAssemblyRefRow(byte[] stringHeap, CLIAssemblyRefTableRow row)
    {
        return new AssemblyIdentity(row.getMajorVersion(), row.getMinorVersion(), row.getBuildNumber(), row.getRevisionNumber(), row.getName().read(stringHeap));
    }

    /**
     * Check whether this assembly matches an assembly reference.
     * @param reference an assembly reference to check this assembly against
     * @return whether this assembly can resolve the reference
     */
    public boolean resolvesRef(AssemblyIdentity reference)
    {
        //The standard is a bit unclear about the necessity of version checks when loading
        //referenced assemblies. Practically, loading multiple assemblies can and will resolve
        //in references to multiple different mscrolibs (a .NET 5 assembly can load a .NET 2 class
        //library assembly), and full strict version checks will crash and burn. While it is possible
        //to just give an exception to the standard library, we decided to ignore versions completely.
        //According to a note in II.6.2.1.4 Version numbers, this seems conforming:
        // A conforming implementation can ignore version numbers entirely, or it can require that they
        // match precisely when binding a reference, or it can exhibit any other behavior deemed appropriate.

        if(!name.equals(reference.name))
            return false;

        return true;

        //In case strict verison checks are required, the code below can be uncommented.
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
