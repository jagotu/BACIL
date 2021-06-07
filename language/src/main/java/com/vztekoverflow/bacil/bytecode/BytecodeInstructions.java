package com.vztekoverflow.bacil.bytecode;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.vztekoverflow.bacil.runtime.EvaluationStackPrimitiveMarker;

import static com.vztekoverflow.bacil.runtime.EvaluationStackPrimitiveMarker.*;

public class BytecodeInstructions {

    //Instruction constants
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

    public static final int CEQ = 0xFE01;
    public static final int CGT = 0xFE02;
    public static final int CGT_UN = 0xFE03;
    public static final int CLT = 0xFE04;
    public static final int CLT_UN = 0xFE05;


    //Custom truffle instructions start here
    //Allowed by III.1.2.1:
    //Opcodes whose first byte lies in the range 0xF0 through 0xFB
    //inclusive, are available for experimental purposes.
    public static final int TRUFFLE_NODE = 0xF0; //Used to replace nodeized instructions



    /**
     * An array that maps from an opcode value to a {@link String} for the corresponding
     * instruction mnemonic.
     */
    @CompilationFinal(dimensions = 1) private static final String[] nameArray = new String[256];

    /**
     * An array that maps from an opcode value to the length in bytes for the corresponding
     * instruction.
     */
    @CompilationFinal(dimensions = 1) private static final int[] lengthArray = new int[256];

    /**
     * An array that maps from an opcode value to the number of slots pushed on the stack by the
     * corresponding instruction.
     *
     * For dynamic instructions (like function calls), 0 should be used.
     */
    @CompilationFinal(dimensions = 1) private static final int[] stackEffectArray = new int[256];


    /**
     * An array that maps from a prefixed opcode value to a {@link String} for the corresponding
     * instruction mnemonic.
     */
    @CompilationFinal(dimensions = 1) private static final String[] prefixedNameArray = new String[256];

    /**
     * An array that maps from a prefixed opcode value to the length in bytes for the corresponding
     * instruction.
     */
    @CompilationFinal(dimensions = 1) private static final int[] prefixedLengthArray = new int[256];

    /**
     * An array that maps from a prefixed opcode value to the number of slots pushed on the stack by the
     * corresponding instruction.
     *
     * For dynamic instructions (like function calls), 0 should be used.
     */
    @CompilationFinal(dimensions = 1) private static final int[] prefixedStackEffectArray = new int[256];


    static {
        //Defining instructions with names, formats and stackEffects.
        //The format string is currently only used to calculate the length,
        //each letter representing one byte.
        //
        //o = opcode
        //i = immediate
        //t = token
        def(NOP, "nop", "o", 0);

        def(LDNULL, "ldnull", "o", 1);
        def(LDC_I4_0, "ldc.i4.0", "o", 1);
        def(LDC_I4_1, "ldc.i4.1", "o", 1);
        def(LDC_I4_2, "ldc.i4.2", "o", 1);
        def(LDC_I4_3, "ldc.i4.3", "o", 1);
        def(LDC_I4_4, "ldc.i4.4", "o", 1);
        def(LDC_I4_5, "ldc.i4.5", "o", 1);
        def(LDC_I4_6, "ldc.i4.6", "o", 1);
        def(LDC_I4_7, "ldc.i4.7", "o", 1);
        def(LDC_I4_8, "ldc.i4.8", "o", 1);
        def(LDC_I4_M1, "ldc.i4.m1", "o", 1);
        def(LDC_I4, "ldc.i4", "oiiii", 1);
        def(LDC_I8, "ldc.i8", "oiiiiiiii", 1);
        def(LDC_R4, "ldc.r4", "oiiii", 1);
        def(LDC_R8, "ldc.r8", "oiiiiiiii", 1);
        def(LDC_I4_S, "ldc.i4.s", "oi", 1);

        def(LDSTR, "ldstr", "otttt", 0);

        def(LDLOC_0, "ldloc.0", "o", 1);
        def(LDLOC_1, "ldloc.1", "o", 1);
        def(LDLOC_2, "ldloc.2", "o", 1);
        def(LDLOC_3, "ldloc.3", "o", 1);
        def(LDLOC_S, "ldloc.s", "oi", 1);
        def(LDLOCA_S, "ldloca.s", "oi", 1);

        def(LDTOKEN, "ldtoken", "otttt", 1);



        def(STLOC_0, "stloc.0", "o", -1);
        def(STLOC_1, "stloc.1", "o", -1);
        def(STLOC_2, "stloc.2", "o", -1);
        def(STLOC_3, "stloc.3", "o", -1);
        def(STLOC_S, "stloc.s", "oi", -1);

        def(LDARG_0, "ldarg.0", "o", 1);
        def(LDARG_1, "ldarg.1", "o", 1);
        def(LDARG_2, "ldarg.2", "o", 1);
        def(LDARG_3, "ldarg.3", "o", 1);
        def(LDARG_S, "ldarg.s", "oi", 1);
        def(LDARGA_S, "ldarga.s", "oi", 1);

        def(STARG_S, "starg.s", "oi", -1);

        def(LDIND_I1, "ldind.i1", "o", 0);
        def(LDIND_U1, "ldind.u1", "o", 0);
        def(LDIND_I2, "ldind.i2", "o", 0);
        def(LDIND_U2, "ldind.u2", "o", 0);
        def(LDIND_I4, "ldind.i4", "o", 0);
        def(LDIND_U4, "ldind.u4", "o", 0);
        def(LDIND_I8, "ldind.i8", "o", 0);
        def(LDIND_I, "ldind.i", "o", 0);
        def(LDIND_R4, "ldind.r4", "o", 0);
        def(LDIND_R8, "ldind.r8", "o", 0);
        def(LDIND_REF, "ldind.ref", "o", 0);

        def(STIND_I1, "stind.i1", "o", -2);
        def(STIND_I2, "stind.i2", "o", -2);
        def(STIND_I4, "stind.i4", "o", -2);
        def(STIND_I8, "stind.i8", "o", -2);
        def(STIND_I, "stind.i", "o", -2);
        def(STIND_R4, "stind.r4", "o", -2);
        def(STIND_R8, "stind.r8", "o", -2);
        def(STIND_REF, "stind.ref", "o", -2);

        def(STFLD, "stfld", "otttt", 0);
        def(LDFLD, "ldfld", "otttt", 0);
        def(LDSFLD, "ldsfld", "otttt", 0);
        def(STSFLD, "stsfld", "otttt", 0);
        def(LDFLDA, "ldflda", "otttt", 0);
        def(LDSFLDA, "ldsfdla", "otttt", 0);

        def(LDELEM, "ldelem", "otttt", 0);
        def(LDELEM_I1,  "ldelem.i1", "o", -1);
        def(LDELEM_U1,  "ldelem.u1", "o", -1);
        def(LDELEM_I2,  "ldelem.i2", "o", -1);
        def(LDELEM_U2,  "ldelem.u2", "o", -1);
        def(LDELEM_I4,  "ldelem.i4", "o", -1);
        def(LDELEM_U4,  "ldelem.u4", "o", -1);
        def(LDELEM_I8,  "ldelem.i8", "o", -1);
        def(LDELEM_I,   "ldelem.i", "o", -1);
        def(LDELEM_R4,  "ldelem.r4", "o", -1);
        def(LDELEM_R8,  "ldelem.r8", "o", -1);
        def(LDELEM_REF, "ldelem.ref", "o", -1);

        def(STELEM,     "stelem", "otttt", 0);
        def(STELEM_I1,  "stelem.i1", "o", -3);
        def(STELEM_I2,  "stelem.i2", "o", -3);
        def(STELEM_I4,  "stelem.i4", "o", -3);
        def(STELEM_I8,  "stelem.i8", "o", -3);
        def(STELEM_I,   "stelem.i", "o", -3);
        def(STELEM_R4,  "stelem.r4", "o", -3);
        def(STELEM_R8,  "stelem.r8", "o", -3);
        def(STELEM_REF, "stelem.ref", "o", -3);

        def(LDELEMA, "ldelema", "otttt", 0);

        def(DUP, "dup", "o", 1);

        def(POP, "pop", "o", -1);

        def(RET, "ret", "o", 0);

        def(BR, "br", "oiiii", 0);
        def(BR_S, "br.s", "oi", 0);

        def(BRFALSE, "brfalse", "oiiii", -1);
        def(BRFALSE_S, "brfalse.s", "oi", -1);
        def(BRTRUE, "brtrue", "oiiii", -1);
        def(BRTRUE_S, "brtrue.s", "oi", -1);

        def(NEG, "neg", "o", 0);

        def(ADD, "add", "o", -1);
        def(SUB, "sub", "o", -1);
        def(MUL, "mul", "o", -1);
        def(DIV, "div", "o", -1);
        def(REM, "rem", "o", -1);

        def(AND, "and", "o", -1);
        def(OR, "or", "o", -1);
        def(XOR, "xor", "o", -1);

        def(SHL, "shl", "o", -1);
        def(SHR, "shr", "o", -1);
        def(SHR_UN, "shr.un", "o", -1);


        def(CALL, "call", "otttt", 0);
        def(NEWOBJ, "newobj", "otttt", 0);
        def(CALLVIRT, "callvirt", "otttt",0);

        def(NEWARR, "newarr", "otttt", 0);
        def(LDLEN, "ldlen", "o", 0);

        defPrefixed(CGT, "cgt", "o", -1);
        defPrefixed(CEQ, "ceq", "o", -1);
        defPrefixed(CLT, "clt", "o", -1);
        defPrefixed(CGT_UN, "cgt.un", "o", -1);
        defPrefixed(CLT_UN, "clt.un", "o", -1);

        def(BEQ, "beq", "otttt", -2);
        def(BGE, "bge", "otttt", -2);
        def(BGT, "bgt", "otttt", -2);
        def(BLE, "ble", "otttt", -2);
        def(BLT, "blt", "otttt", -2);

        def(BEQ_S, "beq.s", "ot", -2);
        def(BGE_S, "bge.s", "ot", -2);
        def(BGT_S, "bgt.s", "ot", -2);
        def(BLE_S, "ble.s", "ot", -2);
        def(BLT_S, "blt.s", "ot", -2);

        def(BGE_UN, "bge.un", "otttt", -2);
        def(BGT_UN, "bgt.un", "otttt", -2);
        def(BLE_UN, "ble.un", "otttt", -2);
        def(BLT_UN, "blt.un", "otttt", -2);
        def(BNE_UN, "bne.un", "otttt", -2);

        def(BGE_UN_S, "bge.un.s", "ot", -2);
        def(BGT_UN_S, "bgt.un.s", "ot", -2);
        def(BLE_UN_S, "ble.un.s", "ot", -2);
        def(BLT_UN_S, "blt.un.s", "ot", -2);
        def(BNE_UN_S, "bne.un.s", "ot", -2);

        def(BOX, "box", "otttt", 0);
        def(UNBOX, "unbox", "otttt", 0);


        def(CONV_I1, "conv.i1", "o", 0);
        def(CONV_I2, "conv.i2", "o", 0);
        def(CONV_I4, "conv.i4", "o", 0);
        def(CONV_I8, "conv.i8", "o", 0);
        def(CONV_R4, "conv.r4", "o", 0);
        def(CONV_R8, "conv.r8", "o", 0);
        def(CONV_U1, "conv.u1", "o", 0);
        def(CONV_U2, "conv.u2", "o", 0);
        def(CONV_U4, "conv.u4", "o", 0);
        def(CONV_U8, "conv.u8", "o", 0);
        def(CONV_I, "conv.i", "o", 0);
        def(CONV_U, "conv.u", "o", 0);

        def(TRUFFLE_NODE, "truffle.node", "oiiii", 0);
    }

    /**
     * Defines an instruction for an opcode by entering recording its name, length and stackEffect.
     *
     * @param opcode the opcode to define
     * @param name mnemonic of the instruction
     * @param format format of the instruction bytes
     * @param stackEffect number of slots pushed on the stack by the instruction
     */
    private static void def(int opcode, String name, String format, int stackEffect) {
        assert nameArray[opcode] == null : "opcode " + opcode + " is already bound to name " + nameArray[opcode];
        nameArray[opcode] = name;
        int instructionLength = format.length();
        lengthArray[opcode] = instructionLength;
        stackEffectArray[opcode] = stackEffect;
    }

    /**
     * Defines an instruction for a prefixed opcode by entering recording its name, length and stackEffect.
     *
     * @param opcode the opcode to define
     * @param name mnemonic of the instruction
     * @param format format of the instruction bytes
     * @param stackEffect number of slots pushed on the stack by the instruction
     */
    private static void defPrefixed(int opcode, String name, String format, int stackEffect) {
        opcode = opcode & 0xFF;
        assert prefixedNameArray[opcode] == null : "opcode " + opcode + " is already bound to name " + prefixedNameArray[opcode];
        prefixedNameArray[opcode] = name;
        int instructionLength = format.length();
        prefixedLengthArray[opcode] = instructionLength;
        prefixedStackEffectArray[opcode] = stackEffect;
    }

    /**
     * Get length of the instruction for the specified opcode.
     * Supports prefixed opcodes.
     *
     * @param opcode the instruction opcode
     * @return length of the instruction for {@code opcode}
     */
    public static int getLength(int opcode) {
        if(opcode > 0xFF)
        {
            return prefixedLengthArray[opcode & 0xFF];
        }
        return lengthArray[opcode];
    }

    /**
     * Get the mnemonic name of the instruction for the specified opcode.
     * Supports prefixed opcodes.
     *
     * @param opcode the instruction opcode
     * @return mnemonic name of the instruction for {@code opcode}
     */
    public static String getName(int opcode)
    {
        if(opcode > 0xFF)
        {
            return prefixedNameArray[opcode & 0xFF];
        }
        return nameArray[opcode];
    }

    /**
     * Get the stack effect of the instruction for the specified opcode.
     * Supports prefixed opcodes.
     *
     * @param opcode the instruction opcode
     * @return stack effect of the instruction for {@code opcode}
     */
    public static int getStackEffect(int opcode)
    {
        if(opcode > 0xFF)
        {
            return prefixedStackEffectArray[opcode & 0xFF];
        }
        return stackEffectArray[opcode];
    }

    /**
     * An implementation of Table III.2: Binary Numeric Operations
     * Stores the result type for A op B, where op is add, div, mul, rem, or sub, for each
     * possible combination of operand types.
     *
     * Maps two {@code EVALUATION_STACK_TAG}s to an {@link EvaluationStackPrimitiveMarker}.
     * Invalid combinations result in a null.
     */
    @CompilationFinal(dimensions = 2)
    public static final EvaluationStackPrimitiveMarker[][] binaryNumericResultTypes = new EvaluationStackPrimitiveMarker[EVALUATION_STACK_TAG_MAX +1][EVALUATION_STACK_TAG_MAX +1];

    /**
     * An implementation of Table III.5: Integer Operations
     * Stores the result type for A op B, where op is and, div.un, not, or, rem.un, xor, for each
     * possible combination of operand types.
     *
     * Maps two {@code EVALUATION_STACK_TAG}s to an {@link EvaluationStackPrimitiveMarker}.
     * Invalid combinations result in a null.
     */
    @CompilationFinal(dimensions = 2)
    public static final EvaluationStackPrimitiveMarker[][] binaryIntegerResultTypes = new EvaluationStackPrimitiveMarker[EVALUATION_STACK_TAG_MAX +1][EVALUATION_STACK_TAG_MAX +1];


    /**
     * Defines a valid combination in the Binary Numeric Operations table.
     * Makes sure all combinations are commutative.
     * @param arg1 an {@code EVALUATION_STACK_TAG} representing the first operand
     * @param arg2 an {@code EVALUATION_STACK_TAG} representing the second operand
     * @param result an {@link EvaluationStackPrimitiveMarker} for the resulting evaluation stack type
     */
    public static void binaryNumericResult(byte arg1, byte arg2, EvaluationStackPrimitiveMarker result)
    {
        binaryNumericResultTypes[arg1][arg2] = result;
        if(arg1 != arg2)
        {
            binaryNumericResultTypes[arg2][arg1] = result;
        }
    }

    /**
     * Defines a valid combination in the Integer Operations table.
     * Makes sure all combinations are commutative.
     * @param arg1 an {@code EVALUATION_STACK_TAG} representing the first operand
     * @param arg2 an {@code EVALUATION_STACK_TAG} representing the second operand
     * @param result an {@link EvaluationStackPrimitiveMarker} for the resulting evaluation stack type
     */
    public static void binaryIntegerResult(byte arg1, byte arg2, EvaluationStackPrimitiveMarker result)
    {
        binaryIntegerResultTypes[arg1][arg2] = result;
        if(arg1 != arg2)
        {
            binaryIntegerResultTypes[arg2][arg1] = result;
        }
    }


    //Define binary numeric operations based on Table III.2: Binary Numeric Operations
    static {
        binaryNumericResult(EVALUATION_STACK_TAG_INT32, EVALUATION_STACK_TAG_INT32, EVALUATION_STACK_INT32);
        binaryNumericResult(EVALUATION_STACK_TAG_INT32, EVALUATION_STACK_TAG_NATIVE_INT, EVALUATION_STACK_NATIVE_INT);
        binaryNumericResult(EVALUATION_STACK_TAG_INT64, EVALUATION_STACK_TAG_INT64, EVALUATION_STACK_INT64);
        binaryNumericResult(EVALUATION_STACK_TAG_F, EVALUATION_STACK_TAG_F, EVALUATION_STACK_F);
    }


    //Define binary integer operations based on Table III.5: Integer Operations
    static {
        binaryIntegerResult(EVALUATION_STACK_TAG_INT32, EVALUATION_STACK_TAG_INT32, EVALUATION_STACK_INT32);
        binaryIntegerResult(EVALUATION_STACK_TAG_INT32, EVALUATION_STACK_TAG_NATIVE_INT, EVALUATION_STACK_NATIVE_INT);
        binaryIntegerResult(EVALUATION_STACK_TAG_INT64, EVALUATION_STACK_TAG_INT64, EVALUATION_STACK_INT64);
    }
}
