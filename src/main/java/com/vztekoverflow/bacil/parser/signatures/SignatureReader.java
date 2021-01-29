package com.vztekoverflow.bacil.parser.signatures;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.parser.BACILParserException;
import com.vztekoverflow.bacil.parser.CompressedInteger;
import com.vztekoverflow.bacil.parser.Positionable;

public class SignatureReader implements Positionable {

    private int position = 0;
    private int lastSize = 0;

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public void setPosition(int position) {
        lastSize = position - this.position;
        this.position = position;
    }

    private int lookahead = Integer.MAX_VALUE;

    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private final byte[] data;

    public SignatureReader(byte[] data) {
        this.data = data;
    }

    private int readInternal()
    {
        return CompressedInteger.read(data, this);
    }

    private int toSigned(int unsigned)
    {
        if((unsigned & 1) == 0)
            return unsigned >> 1;

        switch(lastSize)
        {
            case 1:
                return (unsigned >> 1) | (~0x3F);
            case 2:
                return (unsigned >> 1) | (~0x1FFF);
            case 4:
                return (unsigned >> 1) | (~0xFFFFFFF);
            default:
                throw new BACILInternalError("Unreachable");
        }
    }

    public int getUnsigned()
    {
        if(lookahead != Integer.MAX_VALUE)
        {
            int tmp = lookahead;
            lookahead = Integer.MAX_VALUE;
            return tmp;
        }
        return readInternal();
    }

    public int getSigned()
    {
        return toSigned(getUnsigned());
    }

    public int peekUnsigned()
    {
        if(lookahead == Integer.MAX_VALUE)
        {
            lookahead = readInternal();
        }
        return lookahead;
    }

    public int peekSigned()
    {
        return toSigned(peekUnsigned());
    }


    public void assertUnsigned(int expected, String type)
    {
        int result = getUnsigned();
        if (result != expected) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILParserException(String.format("Unexpected value when parsing %s: expected %d, got %d", type, expected, result));
        }
    }

}
