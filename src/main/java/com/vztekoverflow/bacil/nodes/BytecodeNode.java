package com.vztekoverflow.bacil.nodes;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.bytecode.BytecodeBuffer;
import com.vztekoverflow.bacil.bytecode.BytecodeInstructions;
import com.vztekoverflow.bacil.parser.cil.CILMethod;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.parser.signatures.LocalVarSig;
import com.vztekoverflow.bacil.runtime.ExecutionStackType;
import com.vztekoverflow.bacil.runtime.types.Type;

import java.util.HashMap;
import java.util.HashSet;

import static com.vztekoverflow.bacil.bytecode.BytecodeInstructions.*;

public class BytecodeNode extends Node {

    private final CILMethod method;
    private final BytecodeBuffer bytecodeBuffer;
    private final HashMap<Integer, CallNode> callNodes = new HashMap<>();

    public BytecodeNode(CILMethod method, byte[] bytecode)
    {
        this.method = method;
        this.bytecodeBuffer = new BytecodeBuffer(bytecode);
    }


    @ExplodeLoop(kind = ExplodeLoop.LoopExplosionKind.MERGE_EXPLODE)
    public Object execute(VirtualFrame frame)
    {
        //int slotCount = getMethod().getMaxLocals() + getMethod().getMaxStackSize();



        int stackCount = method.getMaxStack();
        int argsCount = method.getMethodDefSig().getParamTypes().length;
        int varsCount = method.getLocalVarSig().getVarTypes().length;

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

        Type[] localTypes = new Type[varsCount+argsCount];
        Object[] locals = new Object[varsCount+argsCount];


        for(int i = 0; i < varsCount; i++)
        {
            localTypes[i] = method.getLocalVarSig().getVarTypes()[i];
        }

        if(method.isInitLocals())
        {
            for(int i = 0; i < varsCount; i++)
            {
                //TODO initialize if method.initLocals
                locals[i] = localTypes[i].defaultConstructor();
            }
        }

        for(int i = 0; i < argsCount; i++)
        {
            localTypes[varsCount+i] = method.getMethodDefSig().getParamTypes()[i];
            locals[varsCount+i] = args[i];
        }

        CompilerAsserts.partialEvaluationConstant(localTypes);



        int top = 0;
        int pc = 0;

        while (true) {
            int curOpcode = bytecodeBuffer.getOpcode(pc);

            CompilerAsserts.partialEvaluationConstant(top);
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
                    storeStack(primitives, refs, top-1, localTypes, locals, curOpcode - STLOC_0); break;
                case STLOC_S:
                    storeStack(primitives, refs, top-1, localTypes, locals, bytecodeBuffer.getImmUByte(pc)); break;

                case LDLOC_0:
                case LDLOC_1:
                case LDLOC_2:
                case LDLOC_3:
                    loadStack(primitives, refs, top, localTypes, locals, curOpcode - LDLOC_0); break;
                case LDLOC_S:
                    loadStack(primitives, refs, top, localTypes, locals, bytecodeBuffer.getImmUByte(pc)); break;

                case LDARG_0:
                case LDARG_1:
                case LDARG_2:
                case LDARG_3:
                    loadStack(primitives, refs, top, localTypes, locals, varsCount + curOpcode - LDARG_0); break;
                case LDARG_S:
                    loadStack(primitives, refs, top, localTypes, locals, varsCount + bytecodeBuffer.getImmUByte(pc)); break;

                case RET:
                    return getReturnValue(primitives, refs, top-1, method.getMethodDefSig().getRetType());

                case BR:
                    pc = doJmp(bytecodeBuffer, pc, bytecodeBuffer.getImmInt(pc)); continue;
                case BR_S:
                    pc = doJmp(bytecodeBuffer, pc, bytecodeBuffer.getImmByte(pc)); continue;

                case BRTRUE:
                case BRFALSE:
                    pc = conditionalJump(curOpcode, primitives, refs, top-1, bytecodeBuffer, pc, bytecodeBuffer.getImmInt(pc));
                    top += BytecodeInstructions.getStackEffect(curOpcode);
                    continue;

                case BRTRUE_S:
                case BRFALSE_S:
                    pc = conditionalJump(curOpcode, primitives, refs, top-1, bytecodeBuffer, pc, bytecodeBuffer.getImmByte(pc));
                    top += BytecodeInstructions.getStackEffect(curOpcode);
                    continue;

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
                    top = doCallToken(frame, primitives, refs, top, bytecodeBuffer.getImmToken(pc), pc); break;

                default:
                    throw new BACILInternalError(String.format("Unsuppored opcode %d (%s)", curOpcode, BytecodeInstructions.getName(curOpcode)));
            }

            top += BytecodeInstructions.getStackEffect(curOpcode);
            pc = bytecodeBuffer.nextInstruction(pc);
        }

    }

    public static Object[] prepareArgs(long[] primitives, Object[] refs, int top, CILMethod method)
    {
        final int argsCount = method.getMethodDefSig().getParamCount();

        final Object[] args = new Object[argsCount];
        final int firstArg = top - argsCount;

        for(int i = 0; i < argsCount; i++)
        {
            args[i] = method.getMethodDefSig().getParamTypes()[i].fromStackVar(refs[firstArg+i], primitives[firstArg+i]);
        }

        return args;
    }

    public int doCallToken(VirtualFrame frame, long[] primitives, Object[] refs, int top, CLITablePtr token, int pc)
    {
        if(callNodes.containsKey(pc))
        {
            return callNodes.get(pc).execute(frame, primitives, refs);
        }

        final CILMethod toCall = method.getComponent().getLocalMethod(token);
        CallNode node = new CallNode(toCall, top);
        callNodes.put(pc, node);
        return node.execute(frame, primitives, refs);
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

    public static int conditionalJump(int opcode, long[] primitives, Object[] refs, int slot, BytecodeBuffer bytecodeBuffer, int pc, int offset)
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

        if(value)
        {
            return doJmp(bytecodeBuffer, pc, offset);
        }
        return bytecodeBuffer.nextInstruction(pc);
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
                    result = primitives[slot1] - primitives[slot2]; //TODO spravne poradi?
                    break;
                case MUL:
                    result = primitives[slot1] * primitives[slot2];
                    break;
                case DIV:
                    result = primitives[slot1] / primitives[slot2]; //TODO spravne poradi?
                    break;
                case REM:
                    result = primitives[slot1] % primitives[slot2]; //TODO spravne poradi?
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
