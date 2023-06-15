package com.vztekoverflow.cil.parser.cli;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.vztekoverflow.cil.parser.ByteSequenceBuffer;
import com.vztekoverflow.cil.parser.CILParserException;
import com.vztekoverflow.cil.parser.ParserBundle;
import org.graalvm.polyglot.io.ByteSequence;

/**
 * Representation of the CLI metadata as specified in II.24 Metadata physical layout, mainly
 * II.24.2.1 Metadata root.
 */
public class CLIMetadata {
  private final short majorVersion;
  private final short minorVersion;
  private final int reserved;
  private final String version;

  @CompilationFinal(dimensions = 1)
  private final CLIStreamHeader[] streamHeaders;

  private final int metadataStartPosition;

  public CLIMetadata(
      short majorVersion,
      short minorVersion,
      int reserved,
      String version,
      CLIStreamHeader[] streamHeaders,
      int metadataStartPosition) {
    this.majorVersion = majorVersion;
    this.minorVersion = minorVersion;
    this.reserved = reserved;
    this.version = version;
    this.streamHeaders = streamHeaders;
    this.metadataStartPosition = metadataStartPosition;
  }

  /**
   * Read the CLI metadata from the provided {@link ByteSequenceBuffer}.
   *
   * @param buf the byte sequence to read the CLI metadata from
   * @return the CLI metadata represented as a {@link CLIMetadata} instance
   */
  public static CLIMetadata read(ByteSequenceBuffer buf) {
    final int metadataStart = buf.getPosition();
    final int signature = buf.getInt();
    if (signature != 0x424A5342)
      throw new CILParserException(ParserBundle.message("cil.parser.exception.invalidSignature"));

    final short majorVersion = buf.getShort();
    final short minorVersion = buf.getShort();
    final int reserved = buf.getInt();

    final int versionLength = buf.getInt();

    final String versionPadded = buf.getUTF8String(versionLength);
    final String version = versionPadded.substring(0, versionPadded.indexOf(0));

    final short flags = buf.getShort();

    final short streamCount = buf.getShort();

    final CLIStreamHeader[] streamHeaders = new CLIStreamHeader[streamCount];

    for (int i = 0; i < streamCount; i++) {
      streamHeaders[i] = CLIStreamHeader.read(buf);
    }

    return new CLIMetadata(
        majorVersion, minorVersion, reserved, version, streamHeaders, metadataStart);
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

  /** Get a stream header for a stream by name. */
  public CLIStreamHeader getStreamHeader(String streamName) {
    for (CLIStreamHeader header : streamHeaders) {
      if (header.getName().equals(streamName)) return header;
    }
    return null;
  }

  /**
   * Get the stream content for a stream by name as a {@link ByteSequence}.
   *
   * @param streamName the name of the stream to find
   * @param fileBytes a {@link ByteSequence} representing the source file
   * @return a {@link ByteSequence} representing the stream content or null if stream doesn't exist
   */
  public ByteSequence getStream(String streamName, ByteSequence fileBytes) {
    CLIStreamHeader header = getStreamHeader(streamName);

    if (header == null) return null;

    return fileBytes.subSequence(
        metadataStartPosition + header.getOffset(),
        metadataStartPosition + header.getOffset() + header.getSize());
  }

  /**
   * Get the stream content for a stream by name as a byte[].
   *
   * @param streamName the name of the stream to find
   * @param fileBytes a {@link ByteSequence} representing the source file
   * @return a byte[] representing the stream content or an empty byte[] if the stream doesn't exist
   */
  public byte[] getStreamBytes(String streamName, ByteSequence fileBytes) {
    ByteSequence streamData = getStream(streamName, fileBytes);

    if (streamData == null) {
      return new byte[0];
    }

    return streamData.toByteArray();
  }
}
