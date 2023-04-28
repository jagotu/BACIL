package com.vztekoverflow.cil.parser.bytecode;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;

/**
 * A class encapsulating raw bytecode bytes with an API to directly read
 * opcodes (with prefixed opcodes support) and operands, and patch the
 * underlying bytecode.
 *
 * Similar to {@link java.nio.ByteBuffer}, but stateless (not having an internal position stored)
 * and providing only bytecode-specific accessor methods.
 */
public class BytecodeBuffer {

    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private final byte[] bytecode;

    /**
     * Creates a new {@code BytecodeBuffer} for the specified bytecode.
     *
     * @param bytecode the bytecode itself as an array of bytes
     */
    public BytecodeBuffer(byte[] bytecode) {
        this.bytecode = bytecode;
    }

    /**
     * Gets the opcode of the instruction at the specified {@code position}.
     * Supports prefixed opcodes.
     *
     * @param position the index from which to read the opcode
     * @return the integer opcode at {@code position}
     */
    public int getOpcode(int position)
    {
        int bc = (bytecode[position]) & 0xFF;
        if(bc == BytecodeInstructions.PREFIXED)
        {
            return 0xFE00 | (bytecode[position+1]);
        }
        return bc;
    }

    /**
     * Gets the address of the next instruction, handling variable instruction length.
     * Supports prefixed opcodes.
     *
     * @param position the index of an instruction
     * @return the index of the next instruction
     */
    public int nextInstruction(int position)
    {
        int opcode = getOpcode(position);
        if(opcode > 0xFF)
        {
            return position + BytecodeInstructions.getLength(opcode) + 1;
        } else {
            return position + BytecodeInstructions.getLength(opcode);
        }
    }

    /**
     * Reads the immediate value from the instruction at the specified {@code position} as a byte.
     * Supports prefixed opcodes.
     *
     * @param position the index of the instruction
     * @return the immediate value as a byte
     */
    public byte getImmByte(int position)
    {
        return bytecode[position+prefixedOffset(position)+1];
    }

    /**
     * Reads the immediate value from the instruction at the specified {@code position} as an unsigned byte.
     * Returns a short because Java doesn't have unsigned byte support.
     * Supports prefixed opcodes.
     *
     * @param position the index of the instruction
     * @return the immediate value as an unsigned byte
     */
    public short getImmUByte(int position)
    {
        return (short)(getImmByte(position) & 0xFF);
    }

    /**
     * Reads the immediate value from the instruction at the specified {@code position} as a short.
     * Supports prefixed opcodes.
     *
     * @param position the index of the instruction
     * @return the immediate value as a short
     */
    public short getImmShort(int position)
    {
        return Bytes.getShort(bytecode, position+1+prefixedOffset(position));
    }

    /**
     * Reads the immediate value from the instruction at the specified {@code position} as an unsigned short.
     * Returns an int because Java doesn't have unsigned short support.
     * Supports prefixed opcodes.
     *
     * @param position the index of the instruction
     * @return the immediate value as an unsigned short
     */
    public int getImmUShort(int position)
    {
        return (int)(getImmShort(position) & 0xFFFF);
    }

    /**
     * Reads the immediate value from the instruction at the specified {@code position} as an int.
     * Supports prefixed opcodes.
     *
     * @param position the index of the instruction
     * @return the immediate value as an int
     */
    public int getImmInt(int position)
    {
        return Bytes.getInt(bytecode, position+1+prefixedOffset(position));
    }

    /**
     * Reads the immediate value from the instruction at the specified {@code position} as an unsigned int.
     * Returns a long because Java doesn't have unsigned short support.
     * Supports prefixed opcodes.
     *
     * @param position the index of the instruction
     * @return the immediate value as an unsigned int
     */
    public long getImmUInt(int position)
    {
        return (getImmInt(position) & 0xFFFFFFFFL);
    }

    /**
     * Reads the immediate value from the instruction at the specified {@code position} as a long.
     * Supports prefixed opcodes.
     *
     * @param position the index of the instruction
     * @return the immediate value as a long
     */
    public long getImmLong(int position)
    {
        return Bytes.getLong(bytecode, position+1+prefixedOffset(position));
    }

    /**
     * Writes the {@code patch} bytes to the specified position, overwriting the original bytecode.
     *
     * It is the caller's responsibility to never call this method in compilation and invalidate
     * all code built from the original bytecode.
     * @param position the index to write the patch to
     * @param patch the bytes to write at the specified position
     */
    public void patchBytecode(int position, byte[] patch)
    {
        //Assert this isn't a part of compilation, as bytecode patching invalidates all
        //code compiled from the original bytecode.
        CompilerAsserts.neverPartOfCompilation();

        System.arraycopy(patch, 0, bytecode, position, patch.length);
    }

    /**
     * Reads the immediate value from the instruction at the specified {@code position} as a {@link CLITablePtr}.
     * Supports prefixed opcodes.
     *
     * @param position the index of the instruction
     * @return the immediate token as {@link CLITablePtr}
     */
    public CLITablePtr getImmToken(int position)
    {
        return CLITablePtr.fromToken(getImmInt(position));
    }

    /**
     * Returns the number of bytes taken by instruction prefixes.
     * Used for finding the beginning of immediate values.
     *
     * @param position the index of the instruction
     * @return number of bytes taken by instruction prefixes
     */
    private int prefixedOffset(int position)
    {
        if((bytecode[position] & 0xFF) == BytecodeInstructions.PREFIXED)
        {
            return 1;
        } else {
            return 0;
        }
    }
}
