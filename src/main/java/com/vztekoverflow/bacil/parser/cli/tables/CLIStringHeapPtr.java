package com.vztekoverflow.bacil.parser.cli.tables;

import java.nio.charset.StandardCharsets;

public class CLIStringHeapPtr extends CLIHeapPtr<String> {

    public CLIStringHeapPtr(int offset) {
        super(offset);
    }

    @Override
    public String read(byte[] heapData) {
        int nullByteOffset;

        if(offset < 0)
        {
            return "KOKOT";
        }

        for(nullByteOffset = offset;nullByteOffset<heapData.length;nullByteOffset++) {
            if (heapData[nullByteOffset] == 0)
                break;
        }


        return new String(heapData, offset, nullByteOffset-offset, StandardCharsets.UTF_8);
    }
}
