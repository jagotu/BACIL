package com.vztekoverflow.bacil.parser.pe;

import com.vztekoverflow.bacil.parser.ByteSequenceBuffer;

public class PEOptionalHeader {

    public static final int PE32_MAGIC = 0x10B;
    public static final int PE32P_MAGIC = 0x20B;

    private final short magic;
    private final byte majorLinkerVersion;
    private final byte minorLinkerVersion;
    private final int sizeOfCode;
    private final int sizeOfInitializedData;
    private final int sizeOfUninitializedData;
    private final int addressOfEntryPoint;
    private final int baseOfCode;
    private final int baseOfData;
    private final long imageBase;
    private final int sectionAlignment;
    private final int fileAlignment;
    private final short majorOperatingSystemVersion;
    private final short minorOperatingSystemVersion;
    private final short majorImageVersion;
    private final short minorImageVersion;
    private final short majorSubsystemVersion;
    private final short minorSubsystemVersion;
    private final int win32VersionValue;
    private final int sizeOfImage;
    private final int sizeOfHeaders;
    private final int checkSum;
    private final short subsystem;
    private final short dllCharacteristics;
    private final long sizeOfStackReserve;
    private final long sizeOfStackCommit;
    private final long sizeOfHeapReserve;
    private final long sizeOfHeapCommit;
    private final int loaderFlags;
    private final int numberOfRvaAndSizes;

    public PEOptionalHeader(short magic, byte majorLinkerVersion, byte minorLinkerVersion, int sizeOfCode, int sizeOfInitializedData, int sizeOfUninitializedData, int addressOfEntryPoint, int baseOfCode, int baseOfData, long imageBase, int sectionAlignment, int fileAlignment, short majorOperatingSystemVersion, short minorOperatingSystemVersion, short majorImageVersion, short minorImageVersion, short majorSubsystemVersion, short minorSubsystemVersion, int win32VersionValue, int sizeOfImage, int sizeOfHeaders, int checkSum, short subsystem, short dllCharacteristics, long sizeOfStackReserve, long sizeOfStackCommit, long sizeOfHeapReserve, long sizeOfHeapCommit, int loaderFlags, int numberOfRvaAndSizes) {
        this.magic = magic;
        this.majorLinkerVersion = majorLinkerVersion;
        this.minorLinkerVersion = minorLinkerVersion;
        this.sizeOfCode = sizeOfCode;
        this.sizeOfInitializedData = sizeOfInitializedData;
        this.sizeOfUninitializedData = sizeOfUninitializedData;
        this.addressOfEntryPoint = addressOfEntryPoint;
        this.baseOfCode = baseOfCode;
        this.baseOfData = baseOfData;
        this.imageBase = imageBase;
        this.sectionAlignment = sectionAlignment;
        this.fileAlignment = fileAlignment;
        this.majorOperatingSystemVersion = majorOperatingSystemVersion;
        this.minorOperatingSystemVersion = minorOperatingSystemVersion;
        this.majorImageVersion = majorImageVersion;
        this.minorImageVersion = minorImageVersion;
        this.majorSubsystemVersion = majorSubsystemVersion;
        this.minorSubsystemVersion = minorSubsystemVersion;
        this.win32VersionValue = win32VersionValue;
        this.sizeOfImage = sizeOfImage;
        this.sizeOfHeaders = sizeOfHeaders;
        this.checkSum = checkSum;
        this.subsystem = subsystem;
        this.dllCharacteristics = dllCharacteristics;
        this.sizeOfStackReserve = sizeOfStackReserve;
        this.sizeOfStackCommit = sizeOfStackCommit;
        this.sizeOfHeapReserve = sizeOfHeapReserve;
        this.sizeOfHeapCommit = sizeOfHeapCommit;
        this.loaderFlags = loaderFlags;
        this.numberOfRvaAndSizes = numberOfRvaAndSizes;
    }


    public short getMagic() {
        return magic;
    }

    public byte getMajorLinkerVersion() {
        return majorLinkerVersion;
    }

    public byte getMinorLinkerVersion() {
        return minorLinkerVersion;
    }

    public int getSizeOfCode() {
        return sizeOfCode;
    }

    public int getSizeOfInitializedData() {
        return sizeOfInitializedData;
    }

    public int getSizeOfUninitializedData() {
        return sizeOfUninitializedData;
    }

    public int getAddressOfEntryPoint() {
        return addressOfEntryPoint;
    }

    public int getBaseOfCode() {
        return baseOfCode;
    }

    public int getBaseOfData() {
        return baseOfData;
    }

    public long getImageBase() {
        return imageBase;
    }

    public int getSectionAlignment() {
        return sectionAlignment;
    }

    public int getFileAlignment() {
        return fileAlignment;
    }

    public short getMajorOperatingSystemVersion() {
        return majorOperatingSystemVersion;
    }

    public short getMinorOperatingSystemVersion() {
        return minorOperatingSystemVersion;
    }

    public short getMajorImageVersion() {
        return majorImageVersion;
    }

    public short getMinorImageVersion() {
        return minorImageVersion;
    }

    public short getMajorSubsystemVersion() {
        return majorSubsystemVersion;
    }

    public short getMinorSubsystemVersion() {
        return minorSubsystemVersion;
    }

    public int getWin32VersionValue() {
        return win32VersionValue;
    }

    public int getSizeOfImage() {
        return sizeOfImage;
    }

    public int getSizeOfHeaders() {
        return sizeOfHeaders;
    }

    public int getCheckSum() {
        return checkSum;
    }

    public short getSubsystem() {
        return subsystem;
    }

    public short getDllCharacteristics() {
        return dllCharacteristics;
    }

    public long getSizeOfStackReserve() {
        return sizeOfStackReserve;
    }

    public long getSizeOfStackCommit() {
        return sizeOfStackCommit;
    }

    public long getSizeOfHeapReserve() {
        return sizeOfHeapReserve;
    }

    public long getSizeOfHeapCommit() {
        return sizeOfHeapCommit;
    }

    public long getLoaderFlags() {
        return loaderFlags;
    }

    public int getNumberOfRvaAndSizes() {
        return numberOfRvaAndSizes;
    }


    public static PEOptionalHeader read(ByteSequenceBuffer buf)
    {
        final short magic = buf.getShort();
        final byte majorLinkerVersion = buf.getByte();
        final byte minorLinkerVersion = buf.getByte();
        final int sizeOfCode = buf.getInt();
        final int sizeOfInitializedData = buf.getInt();
        final int sizeOfUninitializedData = buf.getInt();
        final int addressOfEntryPoint = buf.getInt();
        final int baseOfCode = buf.getInt();

        final int baseOfData;
        final long imageBase;

        if (magic == PE32P_MAGIC)
        {
            baseOfData = 0;
            imageBase = buf.getLong();
        } else {
            baseOfData = buf.getInt();
            imageBase = buf.getInt();
        }

        final int sectionAlignment = buf.getInt();
        final int fileAlignment = buf.getInt();
        final short majorOperatingSystemVersion = buf.getShort();
        final short minorOperatingSystemVersion = buf.getShort();
        final short majorImageVersion = buf.getShort();
        final short minorImageVersion = buf.getShort();
        final short majorSubsystemVersion = buf.getShort();
        final short minorSubsystemVersion = buf.getShort();
        final int win32VersionValue = buf.getInt();
        final int sizeOfImage = buf.getInt();
        final int sizeOfHeaders = buf.getInt();
        final int checkSum = buf.getInt();
        final short subsystem = buf.getShort();
        final short dllCharacteristics = buf.getShort();

        final long sizeOfStackReserve;
        final long sizeOfStackCommit;
        final long sizeOfHeapReserve;
        final long sizeOfHeapCommit;

        if(magic == PE32P_MAGIC)
        {
            sizeOfStackReserve = buf.getLong();
            sizeOfStackCommit = buf.getLong();
            sizeOfHeapReserve = buf.getLong();
            sizeOfHeapCommit = buf.getLong();
        } else {
            sizeOfStackReserve = buf.getInt();
            sizeOfStackCommit = buf.getInt();
            sizeOfHeapReserve = buf.getInt();
            sizeOfHeapCommit = buf.getInt();
        }

        final int loaderFlags = buf.getInt();
        final int numberOfRvaAndSizes = buf.getInt();

        return new PEOptionalHeader(magic,majorLinkerVersion,minorLinkerVersion,sizeOfCode,sizeOfInitializedData,sizeOfUninitializedData,addressOfEntryPoint,baseOfCode,baseOfData,imageBase,sectionAlignment,fileAlignment,majorOperatingSystemVersion,minorOperatingSystemVersion,majorImageVersion,minorImageVersion,majorSubsystemVersion,minorSubsystemVersion,win32VersionValue,sizeOfImage,sizeOfHeaders,checkSum,subsystem,dllCharacteristics,sizeOfStackReserve,sizeOfStackCommit,sizeOfHeapReserve,sizeOfHeapCommit,loaderFlags,numberOfRvaAndSizes);
    }
}
