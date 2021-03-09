package com.vztekoverflow.bacil.parser.cli;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.vztekoverflow.bacil.parser.BACILParserException;
import com.vztekoverflow.bacil.parser.ByteSequenceBuffer;
import org.graalvm.polyglot.io.ByteSequence;

public class CLIMetadata {
    private final short majorVersion;
    private final short minorVersion;
    private final int reserved;
    private final String version;

    @CompilationFinal(dimensions = 1)
    private final CLIStreamHeader[] streamHeaders;

    private final int metadataStartPosition;

    public CLIMetadata(short majorVersion, short minorVersion, int reserved, String version, CLIStreamHeader[] streamHeaders, int metadataStartPosition) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.reserved = reserved;
        this.version = version;
        this.streamHeaders = streamHeaders;
        this.metadataStartPosition = metadataStartPosition;
    }

    public short getMajorVersion() {
        return majorVersion;
    }

    public short getMinorVersion() {
        return minorVersion;
    }

    public int getReserved() {
        return reserved;
    }

    public String getVersion() {
        return version;
    }

    public CLIStreamHeader[] getStreamHeaders() {
        return streamHeaders;
    }

    public int getMetadataStartPosition() {
        return metadataStartPosition;
    }

    public static CLIMetadata read(ByteSequenceBuffer buf)
    {
        final int metadataStart = buf.getPosition();
        final int signature = buf.getInt();
        if(signature != 0x424A5342)
            throw new BACILParserException("Invalid signature of CLI metadata: " + signature);

        final short majorVersion = buf.getShort();
        final short minorVersion = buf.getShort();
        final int reserved = buf.getInt();

        final int versionLength = buf.getInt();

        final String versionPadded = buf.getUTF8String(versionLength);
        final String version = versionPadded.substring(0, versionPadded.indexOf(0));

        final short flags = buf.getShort();

        final short streamCount = buf.getShort();

        final CLIStreamHeader[] streamHeaders = new CLIStreamHeader[streamCount];


        for(int i = 0;i<streamCount;i++)
        {
            streamHeaders[i] = CLIStreamHeader.read(buf);
        }



        return new CLIMetadata(majorVersion, minorVersion, reserved, version, streamHeaders, metadataStart);
    }

    public CLIStreamHeader getStreamHeader(String streamName)
    {
        for(CLIStreamHeader header : streamHeaders)
        {
            if(header.getName().equals(streamName))
                return header;
        }
        return null;
    }

    public ByteSequence getStream(String streamName, ByteSequence fileBytes)
    {
        CLIStreamHeader header = getStreamHeader(streamName);

        if(header == null)
            return null;

        return fileBytes.subSequence(metadataStartPosition+header.getOffset(), metadataStartPosition+header.getOffset()+header.getSize());
    }

}
