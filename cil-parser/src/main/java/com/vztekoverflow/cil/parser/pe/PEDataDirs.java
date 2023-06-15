package com.vztekoverflow.cil.parser.pe;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.vztekoverflow.cil.parser.ByteSequenceBuffer;
import com.vztekoverflow.cil.parser.RvaSizePair;

/**
 * Class representing PE data directories, as specified in II.25.2.3.3 PE header data directories.
 */
public class PEDataDirs {

  @CompilationFinal(dimensions = 1)
  private final RvaSizePair[] dataDirs;

  public PEDataDirs(RvaSizePair[] dataDirs) {
    this.dataDirs = dataDirs;
  }

  public static PEDataDirs read(ByteSequenceBuffer buf, int count) {
    RvaSizePair[] dataDirs = new RvaSizePair[count];
    for (int i = 0; i < count; i++) {
      dataDirs[i] = RvaSizePair.read(buf);
    }

    return new PEDataDirs(dataDirs);
  }

  public int getRva(int offset) {
    return dataDirs[offset].getRva();
  }

  public int getSize(int offset) {
    return dataDirs[offset].getSize();
  }
}
