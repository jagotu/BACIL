package com.vztekoverflow.bacil.bytecode;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;

public class BytecodeBuffer {

    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private final byte[] bytecode;
    //private int position = 0;

    public BytecodeBuffer(byte[] bytecode) {
        this.bytecode = bytecode;
    }

    public int getOpcode(int position)
    {
        int bc = (bytecode[position]) & 0xFF;
        if(bc == BytecodeInstructions.PREFIXED)
        {
            return 0xFE00 | (bytecode[position+1]);
        }
        return bc;
    }

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

    public byte getImmByte(int position)
    {
        return bytecode[position+1];
    }

    public short getImmUByte(int position)
    {
        return (short)(getImmByte(position) & 0xFF);
    }

    public short getImmShort(int position)
    {
        return Bytes.getShort(bytecode, position+1);
    }

    public int getImmUShort(int position)
    {
        return (int)(getImmShort(position) & 0xFFFF);
    }

    public int getImmInt(int position)
    {
        return Bytes.getInt(bytecode, position+1);
    }

    public long getImmUInt(int position)
    {
        return (getImmInt(position) & 0xFFFFFFFFL);
    }

    public long getImmLong(int position)
    {
        return Bytes.getLong(bytecode, position+1);
    }

    public CLITablePtr getImmToken(int position)
    {
        return CLITablePtr.fromToken(getImmInt(position));
    }
}
