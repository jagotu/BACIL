package com.vztekoverflow.bacil.parser.signatures;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.parser.BACILParserException;
import com.vztekoverflow.bacil.parser.CompressedInteger;
import com.vztekoverflow.bacil.parser.Positionable;

/**
 * A class providing methods for reading signatures from compressed byte arrays with lookahead.
 * The compression is specified in II.23.2 Blobs and signatures
 */
public class SignatureReader implements Positionable {

    private int position = 0;
    private int lastSize = 0;

    /**
     * Get the current position in the buffer
     */
    @Override
    public int getPosition() {
        return position;
    }

    /**
     * Set the current position in the buffer
     */
    @Override
    public void setPosition(int position) {
        lastSize = position - this.position;
        this.position = position;
    }

    //stored lookahead value
    //the compressed integer can only hold values up to 0x1FFFFFFF, so using MAX_VALUE as a "no value" flag
    //won't result in any collision
    private int lookahead = Integer.MAX_VALUE;

    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private final byte[] data;

    /**
     * Create a new signature reader for the specified byte array.
     */
    public SignatureReader(byte[] data) {
        this.data = data;
    }

    /**
     * Internal read function, passing through lookahead.
     */
    private int readInternal()
    {
        return CompressedInteger.read(data, this);
    }

    /**
     * Convert an unsigned value to a signed one.
     */
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

    /**
     * Get the next value as an unsigned integer.
     */
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

    /**
     * Get the next value as a signed integer.
     */
    public int getSigned()
    {
        return toSigned(getUnsigned());
    }

    /**
     * Peek the next value as an unsigned integer.
     */
    public int peekUnsigned()
    {
        if(lookahead == Integer.MAX_VALUE)
        {
            lookahead = readInternal();
        }
        return lookahead;
    }

    /**
     * Peek the next value as a signed integer.
     */
    public int peekSigned()
    {
        return toSigned(peekUnsigned());
    }


    /**
     * Assert the next unsigned value, otherwise throw an exception.
     * @param expected the expected value
     * @param type the type of the parsed signature, used in the exception text
     */
    public void assertUnsigned(int expected, String type)
    {
        int result = getUnsigned();
        if (result != expected) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILParserException(String.format("Unexpected value when parsing %s: expected %d, got %d", type, expected, result));
        }
    }

}
