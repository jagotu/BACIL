package com.vztekoverflow.bacil.bytecode;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.vztekoverflow.bacil.runtime.ExecutionStackPrimitiveMarker;

import static com.vztekoverflow.bacil.runtime.ExecutionStackPrimitiveMarker.*;

public class BytecodeInstructions {

    public static final int NOP = 0x00;
    public static final int BREAK = 0x01;
    public static final int LDARG_0 = 0x02;
    public static final int LDARG_1 = 0x03;
    public static final int LDARG_2 = 0x04;
    public static final int LDARG_3 = 0x05;
    public static final int LDLOC_0 = 0x06;
    public static final int LDLOC_1 = 0x07;
    public static final int LDLOC_2 = 0x08;
    public static final int LDLOC_3 = 0x09;
    public static final int STLOC_0 = 0x0A;
    public static final int STLOC_1 = 0x0B;
    public static final int STLOC_2 = 0x0C;
    public static final int STLOC_3 = 0x0D;
    public static final int LDARG_S = 0x0E;
    public static final int LDARGA_S = 0x0F;
    public static final int STARG_S = 0x10;
    public static final int LDLOC_S = 0x11;
    public static final int LDLOCA_S = 0x12;
    public static final int STLOC_S = 0x13;
    public static final int LDNULL = 0x14;
    public static final int LDC_I4_M1 = 0x15;
    public static final int LDC_I4_0 = 0x16;
    public static final int LDC_I4_1 = 0x17;
    public static final int LDC_I4_2 = 0x18;
    public static final int LDC_I4_3 = 0x19;
    public static final int LDC_I4_4 = 0x1A;
    public static final int LDC_I4_5 = 0x1B;
    public static final int LDC_I4_6 = 0x1C;
    public static final int LDC_I4_7 = 0x1D;
    public static final int LDC_I4_8 = 0x1E;
    public static final int LDC_I4_S = 0x1F;
    public static final int LDC_I4 = 0x20;
    public static final int LDC_I8 = 0x21;
    public static final int LDC_R4 = 0x22;
    public static final int LDC_R8 = 0x23;
    public static final int DUP = 0x25;
    public static final int POP = 0x26;
    public static final int JMP = 0x27;
    public static final int CALL = 0x28;
    public static final int CALLI = 0x29;
    public static final int RET = 0x2A;
    public static final int BR_S = 0x2B;
    public static final int BRFALSE_S = 0x2C;
    public static final int BRTRUE_S = 0x2D;
    public static final int BEQ_S = 0x2E;
    public static final int BGE_S = 0x2F;
    public static final int BGT_S = 0x30;
    public static final int BLE_S = 0x31;
    public static final int BLT_S = 0x32;
    public static final int BNE_UN_S = 0x33;
    public static final int BGE_UN_S = 0x34;
    public static final int BGT_UN_S = 0x35;
    public static final int BLE_UN_S = 0x36;
    public static final int BLT_UN_S = 0x37;
    public static final int BR = 0x38;
    public static final int BRFALSE = 0x39;
    public static final int BRNULL = 0x39;
    public static final int BRZERO = 0x39;
    public static final int BRINST = 0x3A;
    public static final int BRTRUE = 0x3A;
    public static final int BEQ = 0x3B;
    public static final int BGE = 0x3C;
    public static final int BGT = 0x3D;
    public static final int BLE = 0x3E;
    public static final int BLT = 0x3F;
    public static final int BNE_UN = 0x40;
    public static final int BGE_UN = 0x41;
    public static final int BGT_UN = 0x42;
    public static final int BLE_UN = 0x43;
    public static final int BLT_UN = 0x44;
    public static final int SWITCH = 0x45;
    public static final int LDIND_I1 = 0x46;
    public static final int LDIND_U1 = 0x47;
    public static final int LDIND_I2 = 0x48;
    public static final int LDIND_U2 = 0x49;
    public static final int LDIND_I4 = 0x4A;
    public static final int LDIND_U4 = 0x4B;
    public static final int LDIND_I8 = 0x4C;
    public static final int LDIND_I = 0x4D;
    public static final int LDIND_R4 = 0x4E;
    public static final int LDIND_R8 = 0x4F;
    public static final int LDIND_REF = 0x50;
    public static final int STIND_REF = 0x51;
    public static final int STIND_I1 = 0x52;
    public static final int STIND_I2 = 0x53;
    public static final int STIND_I4 = 0x54;
    public static final int STIND_I8 = 0x55;
    public static final int STIND_R4 = 0x56;
    public static final int STIND_R8 = 0x57;
    public static final int ADD = 0x58;
    public static final int SUB = 0x59;
    public static final int MUL = 0x5A;
    public static final int DIV = 0x5B;
    public static final int DIV_UN = 0x5C;
    public static final int REM = 0x5D;
    public static final int REM_UN = 0x5E;
    public static final int AND = 0x5F;
    public static final int OR = 0x60;
    public static final int XOR = 0x61;
    public static final int SHL = 0x62;
    public static final int SHR = 0x63;
    public static final int SHR_UN = 0x64;
    public static final int NEG = 0x65;
    public static final int NOT = 0x66;
    public static final int CONV_I1 = 0x67;
    public static final int CONV_I2 = 0x68;
    public static final int CONV_I4 = 0x69;
    public static final int CONV_I8 = 0x6A;
    public static final int CONV_R4 = 0x6B;
    public static final int CONV_R8 = 0x6C;
    public static final int CONV_U4 = 0x6D;
    public static final int CONV_U8 = 0x6E;
    public static final int CALLVIRT = 0x6F;
    public static final int CPOBJ = 0x70;
    public static final int LDOBJ = 0x71;
    public static final int LDSTR = 0x72;
    public static final int NEWOBJ = 0x73;
    public static final int CASTCLASS = 0x74;
    public static final int ISINST = 0x75;
    public static final int CONV_R_UN = 0x76;
    public static final int UNBOX = 0x79;
    public static final int THROW = 0x7A;
    public static final int LDFLD = 0x7B;
    public static final int LDFLDA = 0x7C;
    public static final int STFLD = 0x7D;
    public static final int LDSFLD = 0x7E;
    public static final int LDSFLDA = 0x7F;
    public static final int STSFLD = 0x80;
    public static final int STOBJ = 0x81;
    public static final int CONV_OVF_I1_UN = 0x82;
    public static final int CONV_OVF_I2_UN = 0x83;
    public static final int CONV_OVF_I4_UN = 0x84;
    public static final int CONV_OVF_I8_UN = 0x85;
    public static final int CONV_OVF_U1_UN = 0x86;
    public static final int CONV_OVF_U2_UN = 0x87;
    public static final int CONV_OVF_U4_UN = 0x88;
    public static final int CONV_OVF_U8_UN = 0x89;
    public static final int CONV_OVF_I_UN = 0x8A;
    public static final int CONV_OVF_U_UN = 0x8B;
    public static final int BOX = 0x8C;
    public static final int NEWARR = 0x8D;
    public static final int LDLEN = 0x8E;
    public static final int LDELEMA = 0x8F;
    public static final int LDELEM_I1 = 0x90;
    public static final int LDELEM_U1 = 0x91;
    public static final int LDELEM_I2 = 0x92;
    public static final int LDELEM_U2 = 0x93;
    public static final int LDELEM_I4 = 0x94;
    public static final int LDELEM_U4 = 0x95;
    public static final int LDELEM_I8 = 0x96;
    public static final int LDELEM_U8 = 0x96;
    public static final int LDELEM_I = 0x97;
    public static final int LDELEM_R4 = 0x98;
    public static final int LDELEM_R8 = 0x99;
    public static final int LDELEM_REF = 0x9A;
    public static final int STELEM_I = 0x9B;
    public static final int STELEM_I1 = 0x9C;
    public static final int STELEM_I2 = 0x9D;
    public static final int STELEM_I4 = 0x9E;
    public static final int STELEM_I8 = 0x9F;
    public static final int STELEM_R4 = 0xA0;
    public static final int STELEM_R8 = 0xA1;
    public static final int STELEM_REF = 0xA2;
    public static final int LDELEM = 0xA3;
    public static final int STELEM = 0xA4;
    public static final int UNBOX_ANY = 0xA5;
    public static final int CONV_OVF_I1 = 0xB3;
    public static final int CONV_OVF_U1 = 0xB4;
    public static final int CONV_OVF_I2 = 0xB5;
    public static final int CONV_OVF_U2 = 0xB6;
    public static final int CONV_OVF_I4 = 0xB7;
    public static final int CONV_OVF_U4 = 0xB8;
    public static final int CONV_OVF_I8 = 0xB9;
    public static final int CONV_OVF_U8 = 0xBA;
    public static final int REFANYVAL = 0xC2;
    public static final int CKFINITE = 0xC3;
    public static final int MKREFANY = 0xC6;
    public static final int LDTOKEN = 0xD0;
    public static final int CONV_U2 = 0xD1;
    public static final int CONV_U1 = 0xD2;
    public static final int CONV_I = 0xD3;
    public static final int CONV_OVF_I = 0xD4;
    public static final int CONV_OVF_U = 0xD5;
    public static final int ADD_OVF = 0xD6;
    public static final int ADD_OVF_UN = 0xD7;
    public static final int MUL_OVF = 0xD8;
    public static final int MUL_OVF_UN = 0xD9;
    public static final int SUB_OVF = 0xDA;
    public static final int SUB_OVF_UN = 0xDB;
    public static final int ENDFAULT = 0xDC;
    public static final int ENDFINALLY = 0xDC;
    public static final int LEAVE = 0xDD;
    public static final int LEAVE_S = 0xDE;
    public static final int STIND_I = 0xDF;
    public static final int CONV_U = 0xE0;

    public static final int MAX = 0xE0;
    public static final int PREFIXED = 0xFE;

    public static final int TRUFFLE_NODE = 0xF0; //allowed by III.1.2.1
    public static final int BACIL_LDFLD = 0xF1;
    public static final int BACIL_STFLD = 0xF2;


    public static final int CEQ = 0xFE01;
    public static final int CGT = 0xFE02;
    public static final int CGT_UN = 0xFE03;
    public static final int CLT = 0xFE04;
    public static final int CLT_UN = 0xFE05;





    /**
     * An array that maps from a bytecode value to a {@link String} for the corresponding
     * instruction mnemonic.
     */
    @CompilationFinal(dimensions = 1) private static final String[] nameArray = new String[256];

    /**
     * An array that maps from a bytecode value to the set of {@link Flags} for the corresponding
     * instruction.
     */
    @CompilationFinal(dimensions = 1) private static final int[] flagsArray = new int[256];

    /**
     * An array that maps from a bytecode value to the length in bytes for the corresponding
     * instruction.
     */
    @CompilationFinal(dimensions = 1) private static final int[] lengthArray = new int[256];

    /**
     * An array that maps from a bytecode value to the number of slots pushed on the stack by the
     * corresponding instruction.
     */
    @CompilationFinal(dimensions = 1) private static final int[] stackEffectArray = new int[256];


    @CompilationFinal(dimensions = 1) private static final String[] prefixedNameArray = new String[256];

    /**
     * An array that maps from a bytecode value to the set of {@link Flags} for the corresponding
     * instruction.
     */
    @CompilationFinal(dimensions = 1) private static final int[] prefixedFlagsArray = new int[256];

    /**
     * An array that maps from a bytecode value to the length in bytes for the corresponding
     * instruction.
     */
    @CompilationFinal(dimensions = 1) private static final int[] prefixedLengthArray = new int[256];

    /**
     * An array that maps from a bytecode value to the number of slots pushed on the stack by the
     * corresponding instruction.
     */
    @CompilationFinal(dimensions = 1) private static final int[] prefixedStackEffectArray = new int[256];


    static {
        def(NOP, "nop", "b", 0);

        def(LDC_I4_0, "ldc.i4.0", "b", 1);
        def(LDC_I4_1, "ldc.i4.1", "b", 1);
        def(LDC_I4_2, "ldc.i4.2", "b", 1);
        def(LDC_I4_3, "ldc.i4.3", "b", 1);
        def(LDC_I4_4, "ldc.i4.4", "b", 1);
        def(LDC_I4_5, "ldc.i4.5", "b", 1);
        def(LDC_I4_6, "ldc.i4.6", "b", 1);
        def(LDC_I4_7, "ldc.i4.7", "b", 1);
        def(LDC_I4_8, "ldc.i4.8", "b", 1);
        def(LDC_I4_M1, "ldc.i4.m1", "b", 1);
        def(LDC_I4, "ldc.i4", "biiii", 1);
        def(LDC_I8, "ldc.i8", "biiiiiiii", 1);
        def(LDC_R4, "ldc.r4", "biiii", 1);
        def(LDC_R8, "ldc.r8", "biiiiiiii", 1);
        def(LDC_I4_S, "ldc.i4.s", "bi", 1);

        def(LDSTR, "ldstr", "btttt", 1);

        def(LDLOC_0, "ldloc.0", "b", 1);
        def(LDLOC_1, "ldloc.1", "b", 1);
        def(LDLOC_2, "ldloc.2", "b", 1);
        def(LDLOC_3, "ldloc.3", "b", 1);
        def(LDLOC_S, "ldloc.s", "bi", 1);
        def(LDLOCA_S, "ldloca.s", "bi", 1);


        def(STLOC_0, "stloc.0", "b", -1);
        def(STLOC_1, "stloc.1", "b", -1);
        def(STLOC_2, "stloc.2", "b", -1);
        def(STLOC_3, "stloc.3", "b", -1);
        def(STLOC_S, "stloc.s", "bi", -1);

        def(LDARG_0, "ldarg.0", "b", 1);
        def(LDARG_1, "ldarg.1", "b", 1);
        def(LDARG_2, "ldarg.2", "b", 1);
        def(LDARG_3, "ldarg.3", "b", 1);
        def(LDARG_S, "ldarg.s", "bi", 1);
        def(LDARGA_S, "ldarga.s", "bi", 1);

        def(LDIND_I1, "ldind.i1", "b", 0);
        def(LDIND_U1, "ldind.u1", "b", 0);
        def(LDIND_I2, "ldind.i2", "b", 0);
        def(LDIND_U2, "ldind.u2", "b", 0);
        def(LDIND_I4, "ldind.i4", "b", 0);
        def(LDIND_U4, "ldind.u4", "b", 0);
        def(LDIND_I8, "ldind.i8", "b", 0);
        def(LDIND_I, "ldind.i", "b", 0);
        def(LDIND_R4, "ldind.r4", "b", 0);
        def(LDIND_R8, "ldind.r8", "b", 0);
        def(LDIND_REF, "ldind.ref", "b", 0);

        def(STIND_I1, "stind.i1", "b", -2);
        def(STIND_I2, "stind.i2", "b", -2);
        def(STIND_I4, "stind.i4", "b", -2);
        def(STIND_I8, "stind.i8", "b", -2);
        def(STIND_I, "stind.i", "b", -2);
        def(STIND_R4, "stind.r4", "b", -2);
        def(STIND_R8, "stind.r8", "b", -2);
        def(STIND_REF, "stind.ref", "b", -2);

        def(STFLD, "stfld", "btttt", 0);
        def(LDFLD, "ldfld", "btttt", 0);
        def(LDSFLD, "ldsfld", "btttt", 0);
        def(STSFLD, "stsfld", "btttt", 0);
        def(LDFLDA, "ldflda", "btttt", 0);
        def(LDSFLDA, "ldsfdla", "btttt", 0);

        def(DUP, "dup", "b", 1);

        def(POP, "pop", "b", -1);







        def(RET, "ret", "b", 0);

        def(BR, "br", "biiii", 0);
        def(BR_S, "br.s", "bi", 0);

        def(BRFALSE, "brfalse", "biiii", -1);
        def(BRFALSE_S, "brfalse.s", "bi", -1);
        def(BRTRUE, "brtrue", "biiii", -1);
        def(BRTRUE_S, "brtrue.s", "bi", -1);

        def(ADD, "add", "b", -1);
        def(SUB, "sub", "b", -1);
        def(MUL, "mul", "b", -1);
        def(DIV, "div", "b", -1);
        def(REM, "rem", "b", -1);


        def(CALL, "call", "btttt", 0);
        def(NEWOBJ, "newobj", "btttt", 0);
        def(CALLVIRT, "callvirt", "btttt",0);

        defPrefixed(CGT, "cgt", "b", -1);
        defPrefixed(CEQ, "ceq", "b", -1);
        defPrefixed(CLT, "clt", "b", -1);

        def(BEQ, "beq", "btttt", -2);
        def(BGE, "bge", "btttt", -2);
        def(BGT, "bgt", "btttt", -2);
        def(BLE, "ble", "btttt", -2);
        def(BLT, "blt", "btttt", -2);

        def(BEQ_S, "beq.s", "bt", -2);
        def(BGE_S, "bge.s", "bt", -2);
        def(BGT_S, "bgt.s", "bt", -2);
        def(BLE_S, "ble.s", "bt", -2);
        def(BLT_S, "blt.s", "bt", -2);

        def(BOX, "box", "btttt", 0);
        def(UNBOX, "unbox", "btttt", 0);




        def(TRUFFLE_NODE, "truffle.node", "biiii", 0);
        def(BACIL_STFLD, "bacil.stfld", "biiii", -2);
        def(BACIL_LDFLD, "bacil.ldfld", "biiii", 0);
    }

    /**
     * Defines a bytecode by entering it into the arrays that record its name, length and flags.
     *
     * @param name instruction name (should be lower case)
     * @param format encodes the length of the instruction
     */
    private static void def(int opcode, String name, String format, int stackEffect) {
        def(opcode, name, format, stackEffect, 0);
    }

    /**
     * Defines a bytecode by entering it into the arrays that record its name, length and flags.
     *
     * @param name instruction name (lower case)
     * @param format encodes the length of the instruction
     * @param flags the set of {@link Flags} associated with the instruction
     */
    private static void def(int opcode, String name, String format, int stackEffect, int flags) {
        assert nameArray[opcode] == null : "opcode " + opcode + " is already bound to name " + nameArray[opcode];
        nameArray[opcode] = name;
        int instructionLength = format.length();
        lengthArray[opcode] = instructionLength;
        stackEffectArray[opcode] = stackEffect;
        flagsArray[opcode] = flags;

    }

    private static void defPrefixed(int opcode, String name, String format, int stackEffect) {
        defPrefixed(opcode, name, format, stackEffect, 0);
    }


    private static void defPrefixed(int opcode, String name, String format, int stackEffect, int flags) {
        opcode = opcode & 0xFF;
        assert prefixedNameArray[opcode] == null : "opcode " + opcode + " is already bound to name " + prefixedNameArray[opcode];
        prefixedNameArray[opcode] = name;
        int instructionLength = format.length();
        prefixedLengthArray[opcode] = instructionLength;
        prefixedStackEffectArray[opcode] = stackEffect;
        prefixedFlagsArray[opcode] = flags;

    }

    public static int getLength(int opcode) {
        if(opcode > 0xFF)
        {
            return prefixedLengthArray[opcode & 0xFF];
        }
        return lengthArray[opcode];
    }

    public static String getName(int opcode)
    {
        if(opcode > 0xFF)
        {
            return prefixedNameArray[opcode & 0xFF];
        }
        return nameArray[opcode];
    }

    public static int getStackEffect(int opcode)
    {
        if(opcode > 0xFF)
        {
            return prefixedStackEffectArray[opcode & 0xFF];
        }
        return stackEffectArray[opcode];
    }

    @CompilationFinal(dimensions = 2)
    public static final ExecutionStackPrimitiveMarker[][] binaryNumericResultTypes = new ExecutionStackPrimitiveMarker[EXECUTION_STACK_TAG_MAX+1][EXECUTION_STACK_TAG_MAX+1];

    public static void binaryNumericResult(byte arg1, byte arg2, ExecutionStackPrimitiveMarker result)
    {
        binaryNumericResultTypes[arg1][arg2] = result;
        if(arg1 != arg2)
        {
            binaryNumericResultTypes[arg2][arg1] = result;
        }
    }

    //Table III.2: Binary Numeric Operations
    static {
        binaryNumericResult(EXECUTION_STACK_TAG_INT32, EXECUTION_STACK_TAG_INT32, EXECUTION_STACK_INT32);
        binaryNumericResult(EXECUTION_STACK_TAG_INT32, EXECUTION_STACK_TAG_NATIVE_INT, EXECUTION_STACK_NATIVE_INT);
        binaryNumericResult(EXECUTION_STACK_TAG_INT64, EXECUTION_STACK_TAG_INT64, EXECUTION_STACK_INT64);
        binaryNumericResult(EXECUTION_STACK_TAG_F, EXECUTION_STACK_TAG_F, EXECUTION_STACK_F);
    }
}
