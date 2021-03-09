package com.vztekoverflow.bacil.parser.pe;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.vztekoverflow.bacil.parser.ByteSequenceBuffer;
import com.vztekoverflow.bacil.parser.RvaSizePair;

public class PEDataDirs {

    public PEDataDirs (RvaSizePair[] dataDirs) {
        this.dataDirs = dataDirs;
    }

    @CompilationFinal(dimensions = 1)
    private final RvaSizePair[] dataDirs;

    public int getRva(int offset)
    {
        return dataDirs[offset].getRva();
    }

    public int getSize(int offset)
    {
        return dataDirs[offset].getSize();
    }

    public static PEDataDirs read(ByteSequenceBuffer buf, int count)
    {
        RvaSizePair[] dataDirs = new RvaSizePair[count];
        for(int i = 0;i<count;i++)
        {
            dataDirs[i] = RvaSizePair.read(buf);
        }

        return new PEDataDirs(dataDirs);
    }


}
