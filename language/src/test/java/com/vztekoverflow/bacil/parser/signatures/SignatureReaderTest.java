package com.vztekoverflow.bacil.parser.signatures;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SignatureReaderTest {

    //Examples from II.23.2 of ECMA-335 (6th edition)

    @Test
    public void getUnsigned() {
        byte[] data = {0x03, 0x7F, (byte) 0x80, (byte) 0x80, (byte) 0xAE, 0x57, (byte) 0xBF, (byte) 0xFF, (byte) 0xC0, 0x00, 0x40, 0x00, (byte) 0xDF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        SignatureReader reader = new SignatureReader(data);
        assertEquals(3, reader.getUnsigned());
        assertEquals(0x7F, reader.getUnsigned());
        assertEquals(0x80, reader.getUnsigned());
        assertEquals(0x2E57, reader.getUnsigned());
        assertEquals(0x3FFF, reader.getUnsigned());
        assertEquals(0x4000, reader.getUnsigned());
        assertEquals(0x1FFFFFFF, reader.getUnsigned());
    }

    @Test
    public void getSigned() {
        byte[] data = {0x06, 0x7B, (byte) 0x80, (byte) 0x80, 0x01, (byte) 0xC0, 0x00, 0x40, 0x00, (byte) 0x80, 0x01, (byte) 0xDF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFE, (byte) 0xC0, 0x00, 0x00, 0x01};
        SignatureReader reader = new SignatureReader(data);
        assertEquals(3, reader.getSigned());
        assertEquals(-3, reader.getSigned());
        assertEquals(64, reader.getSigned());
        assertEquals(-64, reader.getSigned());
        assertEquals(8192, reader.getSigned());
        assertEquals(-8192, reader.getSigned());
        assertEquals(268435455, reader.getSigned());
        assertEquals(-268435456, reader.getSigned());
    }

    @Test
    public void peekTest()
    {
        byte[] data = {0x06, 0x7B};
        SignatureReader reader = new SignatureReader(data);
        assertEquals(6, reader.peekUnsigned());
        assertEquals(3, reader.peekSigned());
        assertEquals(6, reader.peekUnsigned());
        assertEquals(3, reader.peekSigned());
        assertEquals(3, reader.getSigned());

        assertEquals(-3, reader.peekSigned());
        assertEquals(0x7B, reader.peekUnsigned());
        assertEquals(-3, reader.peekSigned());
        assertEquals(0x7B, reader.peekUnsigned());
        assertEquals(0x7B, reader.getUnsigned());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void readTooMuch()
    {
        byte[] data = {0x06};
        SignatureReader reader = new SignatureReader(data);
        assertEquals(6, reader.getUnsigned());
        reader.getUnsigned();
    }
}