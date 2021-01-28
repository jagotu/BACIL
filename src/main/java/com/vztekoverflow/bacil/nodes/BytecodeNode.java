package com.vztekoverflow.bacil.nodes;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.bytecode.BytecodeBuffer;
import com.vztekoverflow.bacil.bytecode.BytecodeInstructions;
import com.vztekoverflow.bacil.parser.cil.CILMethod;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.runtime.ExecutionStackType;
import com.vztekoverflow.bacil.runtime.types.Type;

import java.util.Arrays;

import static com.vztekoverflow.bacil.bytecode.BytecodeInstructions.*;

public class BytecodeNode extends Node {

    private final CILMethod method;
    private final BytecodeBuffer bytecodeBuffer;
    private final int argsCount;
    private final int varsCount;

    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private final Type[] localsTypes;

    @Children private CallNode[] nodes = new CallNode[0];

    public BytecodeNode(CILMethod method, byte[] bytecode)
    {
        this.method = method;
        this.bytecodeBuffer = new BytecodeBuffer(bytecode);
        argsCount = method.getMethodDefSig().getParamTypes().length;
        varsCount = method.getLocalVarSig().getVarTypes().length;
        localsTypes = new Type[varsCount+argsCount];

        for(int i = 0; i < varsCount; i++)
        {
            localsTypes[i] = method.getLocalVarSig().getVarTypes()[i];
        }

        for(int i = 0; i < argsCount; i++)
        {
            localsTypes[varsCount+i] = method.getMethodDefSig().getParamTypes()[i];
        }

    }




    @ExplodeLoop(kind = ExplodeLoop.LoopExplosionKind.MERGE_EXPLODE)
    public Object execute(VirtualFrame frame)
    {
        //int slotCount = getMethod().getMaxLocals() + getMethod().getMaxStackSize();



        int stackCount = method.getMaxStack();


        Object[] args = frame.getArguments();
        if(args.length != argsCount)
        {
            throw new BACILInternalError("Unexpected number of arguments!");
        }

        CompilerAsserts.partialEvaluationConstant(stackCount);

        //for primitives the refs stack is filled with ExecutionStackType objects
        //that allow tracking of int64/int32/native int/F
        long[] primitives = new long[stackCount];
        Object[] refs = new Object[stackCount];


        CompilerAsserts.partialEvaluationConstant(argsCount);
        CompilerAsserts.partialEvaluationConstant(varsCount);

        Object[] locals = new Object[varsCount+argsCount];


        prepareLocals(argsCount, varsCount, localsTypes, method, locals, args);



        int top = 0;
        int pc = 0;

        loop: while (true) {
            int curOpcode = bytecodeBuffer.getOpcode(pc);

            CompilerAsserts.partialEvaluationConstant(top);
            CompilerAsserts.partialEvaluationConstant(pc);
            CompilerAsserts.partialEvaluationConstant(curOpcode);

            //debug
            //System.out.printf("%s:%04x %s\n", method.getName(), pc, BytecodeInstructions.getName(curOpcode));

            switch(curOpcode) {
                case NOP: break;

                case LDC_I4_M1:
                case LDC_I4_0:
                case LDC_I4_1:
                case LDC_I4_2:
                case LDC_I4_3:
                case LDC_I4_4:
                case LDC_I4_5:
                case LDC_I4_6:
                case LDC_I4_7:
                case LDC_I4_8:
                    putInt32(primitives, refs, top, curOpcode - LDC_I4_0); break;

                case LDC_I4: putInt32(primitives, refs, top, bytecodeBuffer.getImmInt(pc)); break;
                case LDC_I4_S: putInt32(primitives, refs, top, bytecodeBuffer.getImmByte(pc)); break;
                case LDC_I8: putInt64(primitives, refs, top, bytecodeBuffer.getImmLong(pc)); break;

                case STLOC_0:
                case STLOC_1:
                case STLOC_2:
                case STLOC_3:
                    storeStack(primitives, refs, top-1, localsTypes, locals, curOpcode - STLOC_0); break;
                case STLOC_S:
                    storeStack(primitives, refs, top-1, localsTypes, locals, bytecodeBuffer.getImmUByte(pc)); break;

                case LDLOC_0:
                case LDLOC_1:
                case LDLOC_2:
                case LDLOC_3:
                    loadStack(primitives, refs, top, localsTypes, locals, curOpcode - LDLOC_0); break;
                case LDLOC_S:
                    loadStack(primitives, refs, top, localsTypes, locals, bytecodeBuffer.getImmUByte(pc)); break;

                case LDARG_0:
                case LDARG_1:
                case LDARG_2:
                case LDARG_3:
                    loadStack(primitives, refs, top, localsTypes, locals, varsCount + curOpcode - LDARG_0); break;
                case LDARG_S:
                    loadStack(primitives, refs, top, localsTypes, locals, varsCount + bytecodeBuffer.getImmUByte(pc)); break;

                case RET:
                    return getReturnValue(primitives, refs, top-1, method.getMethodDefSig().getRetType());

                case BR:
                    pc = doJmp(bytecodeBuffer, pc, bytecodeBuffer.getImmInt(pc)); continue loop;
                case BR_S:
                    pc = doJmp(bytecodeBuffer, pc, bytecodeBuffer.getImmByte(pc));  continue loop;

                case BRTRUE:
                case BRFALSE:
                    if(shouldBranch(curOpcode, primitives, refs, top-1))
                    {
                        pc = doJmp(bytecodeBuffer, pc, bytecodeBuffer.getImmInt(pc));
                        top += BytecodeInstructions.getStackEffect(curOpcode);
                        continue loop;
                    }
                    break;

                case BRTRUE_S:
                case BRFALSE_S:
                    if(shouldBranch(curOpcode, primitives, refs, top-1))
                    {
                        pc = doJmp(bytecodeBuffer, pc, bytecodeBuffer.getImmByte(pc));
                        top += BytecodeInstructions.getStackEffect(curOpcode);
                        continue loop;
                    }
                    break;

                case ADD:
                case SUB:
                case MUL:
                case DIV:
                case REM:
                    doNumericBinary(curOpcode, primitives, refs, top-2, top-1); break;

                case CEQ:
                case CGT:
                case CLT:
                    doCompareBinary(curOpcode, primitives, refs, top-2, top-1); break;

                case CALL:
                    top = nodeizeCallToken(frame, primitives, refs, top, bytecodeBuffer.getImmToken(pc), pc, curOpcode); break;

                case TRUFFLE_NODE:
                    top = nodes[bytecodeBuffer.getImmInt(pc)].execute(frame, primitives, refs); break;

                default:
                    CompilerDirectives.transferToInterpreterAndInvalidate();
                    throw new BACILInternalError(String.format("Unsuppored opcode %d (%s)", curOpcode, BytecodeInstructions.getName(curOpcode)));
            }

            top += BytecodeInstructions.getStackEffect(curOpcode);
            pc = bytecodeBuffer.nextInstruction(pc);
        }

    }

    @ExplodeLoop
    public static void prepareLocals(int argsCount, int varsCount, Type[] localTypes, CILMethod method, Object[] locals, Object[] args)
    {
        if(method.isInitLocals())
        {
            for(int i = 0; i < varsCount; i++)
            {
                locals[i] = localTypes[i].defaultConstructor();
            }
        }

        if (argsCount >= 0) System.arraycopy(args, 0, locals, varsCount, argsCount);
    }

    @ExplodeLoop
    public static Object[] prepareArgs(long[] primitives, Object[] refs, int top, CILMethod method)
    {

        final int argsCount = method.getMethodDefSig().getParamCount();
        final Object[] args = new Object[argsCount];
        final int firstArg = top - argsCount;
        final Type[] targetTypes = method.getMethodDefSig().getParamTypes();

        CompilerAsserts.partialEvaluationConstant(argsCount);


        for(int i = 0; i < argsCount; i++)
        {
            args[i] = targetTypes[i].fromStackVar(refs[firstArg+i], primitives[firstArg+i]);
        }

        return args;
    }

    private int addNode(CallNode node)
    {
        nodes = Arrays.copyOf(nodes, nodes.length + 1);
        int nodeIndex = nodes.length - 1; // latest empty slot
        nodes[nodeIndex] = insert(node);
        return nodeIndex;
    }

    private static byte[] preparePatch(byte opcode, int imm, int targetLength)
    {
        byte[] patch = new byte[targetLength];
        patch[0] = opcode;
        patch[1] = (byte)(imm & 0xFF);
        patch[2] = (byte)((imm >> 8) & 0xFF);
        patch[3] = (byte)((imm >> 16) & 0xFF);
        patch[4] = (byte)((imm >> 24) & 0xFF);
        return patch;
    }

    public int nodeizeCallToken(VirtualFrame frame, long[] primitives, Object[] refs, int top, CLITablePtr token, int pc, int opcode)
    {

        CompilerDirectives.transferToInterpreterAndInvalidate(); // because we are about to change something that is compilation final
        final CILMethod toCall = method.getComponent().getLocalMethod(token);
        CallNode node = new CallNode(toCall, top);
        int index = addNode(node);
        byte[] patch = preparePatch((byte)TRUFFLE_NODE, index, BytecodeInstructions.getLength(opcode));
        bytecodeBuffer.patchBytecode(pc, patch);

        return nodes[index].execute(frame, primitives, refs);


        //assume local method for now
        /*final CILMethod toCall = method.getComponent().getLocalMethod(token);

        Object returnValue = toCall.getCallTarget().call(args);
        Type returnType = toCall.getMethodDefSig().getRetType();

        if(returnType.getTypeCategory() != Type.ELEMENT_TYPE_VOID)
        {
            returnType.toStackVar(refs, primitives, firstArg, returnValue);
            return firstArg+1;
        }
        return firstArg;*/
    }

    public static boolean shouldBranch(int opcode, long[] primitives, Object[] refs, int slot)
    {
        boolean value;
        if(ExecutionStackType.isExecutionStackType(refs[slot]))
        {
            value = primitives[slot] != 0;
        } else {
            value = refs[slot] != null;
        }

        if(opcode == BRFALSE || opcode == BRFALSE_S)
        {
            value = !value;
        }

        return value;
    }

    public static void doCompareBinary(int opcode, long[] primitives, Object[] refs, int slot1, int slot2)
    {
        //TODO floaty!
        if(ExecutionStackType.isExecutionStackType(refs[slot1]) && ExecutionStackType.isExecutionStackType(refs[slot2]))
        {
            //using the numeric binary table here as it seems to be the same so far
            ExecutionStackType resultType = binaryNumericResultTypes[((ExecutionStackType)refs[slot1]).getTag()][((ExecutionStackType)refs[slot2]).getTag()];
            if(resultType == null)
            {
                throw new BACILInternalError("Invalid types for comparison");
            }

            boolean result = false;

            switch(opcode)
            {
                case CGT:
                    result = primitives[slot1] > primitives[slot2];
                    break;
                case CLT:
                    result = primitives[slot1] < primitives[slot2];
                    break;
                case CEQ:
                    result = primitives[slot1] == primitives[slot2];
                    break;
            }

            primitives[slot1] = result ? 1 : 0;
            refs[slot1] = ExecutionStackType.EXECUTION_STACK_INT32;

        } else {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("Unimplemented.");
        }
    }

    public static void doNumericBinary(int opcode, long[] primitives, Object[] refs, int slot1, int slot2)
    {
        //TODO floaty!
        if(ExecutionStackType.isExecutionStackType(refs[slot1]) && ExecutionStackType.isExecutionStackType(refs[slot2]))
        {
            ExecutionStackType resultType = binaryNumericResultTypes[((ExecutionStackType)refs[slot1]).getTag()][((ExecutionStackType)refs[slot2]).getTag()];
            if(resultType == null)
            {
                throw new BACILInternalError("These types can't be args of numeric binary");
            }
            long result = 0;
            switch(opcode)
            {
                case ADD:
                    result = primitives[slot1] + primitives[slot2];
                    break;
                case SUB:
                    result = primitives[slot1] - primitives[slot2];
                    break;
                case MUL:
                    result = primitives[slot1] * primitives[slot2];
                    break;
                case DIV:
                    result = primitives[slot1] / primitives[slot2];
                    break;
                case REM:
                    result = primitives[slot1] % primitives[slot2];
                    break;

            }

            if(resultType == ExecutionStackType.EXECUTION_STACK_INT32)
            {
                result &= 0xFFFFFFFFL;
            }

            primitives[slot1] = result;
            refs[slot1] = resultType;

        }  else {
            throw new BACILInternalError("Unimplemented.");
        }
    }

    public static int doJmp(BytecodeBuffer bytecodeBuffer, int pc, int offset)
    {
        return bytecodeBuffer.nextInstruction(pc) + offset;
    }

    public static Object getReturnValue(long[] primitives, Object[] refs, int slot, Type retType)
    {
        if(retType.getTypeCategory() == Type.ELEMENT_TYPE_VOID)
            return null; //TODO polyglot API vyzaduje vraceni neceho chytreho

        return retType.fromStackVar(refs[slot], primitives[slot]);
    }

    public static void loadStack(long[] primitives, Object[] refs, int slot, Type[] localTypes, Object[] locals, int localSlot)
    {
        localTypes[localSlot].toStackVar(refs, primitives, slot, locals[localSlot]);
    }

    public static void storeStack(long[] primitives, Object[] refs, int slot, Type[] localTypes, Object[] locals, int localSlot)
    {
        locals[localSlot] = localTypes[localSlot].fromStackVar(refs[slot], primitives[slot]);
    }

    public static void putInt32(long[] primitives, Object[] refs, int slot, int value)
    {
        primitives[slot] = value;
        refs[slot] = ExecutionStackType.EXECUTION_STACK_INT32;
    }

    public static void putInt64(long[] primitives, Object[] refs, int slot, long value)
    {
        primitives[slot] = value;
        refs[slot] = ExecutionStackType.EXECUTION_STACK_INT64;
    }

    public CILMethod getMethod() {
        return method;
    }
}
