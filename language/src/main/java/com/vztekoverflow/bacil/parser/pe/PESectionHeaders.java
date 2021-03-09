package com.vztekoverflow.bacil.parser.pe;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.vztekoverflow.bacil.parser.ByteSequenceBuffer;

public class PESectionHeaders {

    @CompilationFinal(dimensions = 1)
    private final PESectionHeader[] sectionHeaders;

    public PESectionHeaders(PESectionHeader[] sectionHeaders) {
        this.sectionHeaders = sectionHeaders;
    }

    public PESectionHeader[] getSectionHeaders() {
        return sectionHeaders;
    }

    public static PESectionHeaders read(ByteSequenceBuffer buf, int count)
    {
        PESectionHeader[] sectionHeaders = new PESectionHeader[count];
        for(int i = 0; i < count; i++)
        {
            sectionHeaders[i] = PESectionHeader.read(buf);
        }
        return new PESectionHeaders(sectionHeaders);
    }

    public PESectionHeader getSectionForRVA(int RVA)
    {
        for (PESectionHeader sectionHeader : sectionHeaders)
        {
            if(sectionHeader.getVirtualAddress() <= RVA &&
                    sectionHeader.getVirtualAddress() + sectionHeader.getVirtualSize() > RVA)
                return sectionHeader;
        }
        return null;
    }


}
