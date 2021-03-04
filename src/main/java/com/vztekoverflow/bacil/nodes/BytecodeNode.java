package com.vztekoverflow.bacil.nodes;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.BACILLanguage;
import com.vztekoverflow.bacil.bytecode.BytecodeBuffer;
import com.vztekoverflow.bacil.bytecode.BytecodeInstructions;
import com.vztekoverflow.bacil.parser.cil.CILMethod;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.parser.cli.tables.CLIUSHeapPtr;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLIMemberRefTableRow;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITableConstants;
import com.vztekoverflow.bacil.runtime.BACILMethod;
import com.vztekoverflow.bacil.runtime.ExecutionStackPrimitiveMarker;
import com.vztekoverflow.bacil.runtime.LocationReference;
import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.TypeHelpers;
import com.vztekoverflow.bacil.runtime.types.builtin.BuiltinTypes;
import com.vztekoverflow.bacil.runtime.types.builtin.SystemVoidType;
import com.vztekoverflow.bacil.runtime.types.locations.LocationsDescriptor;
import com.vztekoverflow.bacil.runtime.types.locations.LocationsHolder;

import java.util.Arrays;

import static com.vztekoverflow.bacil.bytecode.BytecodeInstructions.*;

public class BytecodeNode extends Node {

    private final CILMethod method;
    private final BytecodeBuffer bytecodeBuffer;
    private final BuiltinTypes builtinTypes;



    @Children private CallableNode[] nodes = new CallableNode[0];

    public BytecodeNode(CILMethod method, byte[] bytecode)
    {
        this.method = method;
        this.bytecodeBuffer = new BytecodeBuffer(bytecode);
        this.builtinTypes = method.getComponent().getBuiltinTypes();
    }




    @ExplodeLoop(kind = ExplodeLoop.LoopExplosionKind.MERGE_EXPLODE)
    public Object execute(VirtualFrame frame)
    {
        //int slotCount = getMethod().getMaxLocals() + getMethod().getMaxStackSize();


        int stackCount = method.getMaxStack();


        Object[] args = frame.getArguments();
        if(args.length != method.getArgsCount())
        {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("Unexpected number of arguments!");
        }

        CompilerAsserts.partialEvaluationConstant(stackCount);

        //for primitives the refs stack is filled with ExecutionStackType objects
        //that allow tracking of int64/int32/native int/F
        long[] primitives = new long[stackCount];
        Object[] refs = new Object[stackCount];

        final int argsCount = method.getArgsCount();
        final int varsCount = method.getVarsCount();


        CompilerAsserts.partialEvaluationConstant(argsCount);
        CompilerAsserts.partialEvaluationConstant(varsCount);


        final LocationsDescriptor descriptor = method.getLocationDescriptor();
        final LocationsHolder locations = new LocationsHolder(descriptor); //I.8.3

        CompilerAsserts.partialEvaluationConstant(descriptor);

        loadArgs(descriptor, locations, argsCount, varsCount, args);
        //initLocations(argsCount, varsCount, locationsTypes, method, locations, args);



        int top = 0;
        int pc = 0;





        loop: while (true) {
            CompilerAsserts.partialEvaluationConstant(pc);
            int curOpcode = bytecodeBuffer.getOpcode(pc);
            CompilerAsserts.partialEvaluationConstant(curOpcode);
            int nextpc = bytecodeBuffer.nextInstruction(pc);

            CompilerAsserts.partialEvaluationConstant(top);


            CompilerAsserts.partialEvaluationConstant(nextpc);

            CompilerDirectives.ensureVirtualized(locations.getPrimitives());
            CompilerDirectives.ensureVirtualized(locations.getRefs());
            CompilerDirectives.ensureVirtualized(refs);
            CompilerDirectives.ensureVirtualized(primitives);



            //debug
            //System.out.printf("%s:%04x %s\n", method.getName(), pc, BytecodeInstructions.getName(curOpcode));

            switch(curOpcode) {
                case NOP:
                case POP:
                    break;

                case LDNULL:
                    refs[top] = null; break;
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


                case LDSTR: putStr(primitives, refs, top, bytecodeBuffer.getImmToken(pc)); break;

                case STLOC_0:
                case STLOC_1:
                case STLOC_2:
                case STLOC_3:
                    storeStack(primitives, refs, top-1, descriptor, locations, curOpcode - STLOC_0); break;
                case STLOC_S:
                    storeStack(primitives, refs, top-1, descriptor, locations, bytecodeBuffer.getImmUByte(pc)); break;

                case LDLOC_0:
                case LDLOC_1:
                case LDLOC_2:
                case LDLOC_3:
                    loadStack(primitives, refs, top, descriptor, locations, curOpcode - LDLOC_0); break;
                case LDLOC_S:
                    loadStack(primitives, refs, top, descriptor, locations, bytecodeBuffer.getImmUByte(pc)); break;

                case LDLOCA_S:
                    refs[top] = getLocalReference(descriptor, locations, bytecodeBuffer.getImmUByte(pc)); break;

                case LDARG_0:
                case LDARG_1:
                case LDARG_2:
                case LDARG_3:
                    loadStack(primitives, refs, top, descriptor, locations, varsCount + curOpcode - LDARG_0); break;
                case LDARG_S:
                    loadStack(primitives, refs, top, descriptor, locations, varsCount + bytecodeBuffer.getImmUByte(pc)); break;

                case LDARGA_S:
                    refs[top] = getLocalReference(descriptor, locations,varsCount + bytecodeBuffer.getImmUByte(pc)); break;


                case STIND_I1:
                case STIND_I2:
                case STIND_I4:
                case STIND_I8:
                case STIND_I:
                case STIND_R4:
                case STIND_R8:
                case STIND_REF:
                    storeIndirect(primitives[top-1], refs[top-1], (LocationReference) refs[top-2], builtinTypes.getForIndirectOpcode(curOpcode)); break;

                case LDIND_I1:
                case LDIND_U1:
                case LDIND_I2:
                case LDIND_U2:
                case LDIND_I4:
                case LDIND_U4:
                case LDIND_I8:
                case LDIND_I:
                case LDIND_R4:
                case LDIND_R8:
                case LDIND_REF:
                    loadIndirect(primitives, refs, top-1, (LocationReference) refs[top-1], builtinTypes.getForIndirectOpcode(curOpcode)); break;

                case DUP:
                    refs[top]=refs[top-1];primitives[top]=primitives[top-1]; break;




                case RET:
                    return getReturnValue(primitives, refs, top-1, method.getMethodDefSig().getRetType());

                case BR:
                    pc = nextpc + bytecodeBuffer.getImmInt(pc); continue loop;
                case BR_S:
                    pc = nextpc + bytecodeBuffer.getImmByte(pc);  continue loop;


                case BEQ:
                case BGE:
                case BGT:
                case BLE:
                case BLT:
                    if(binaryCompareResult(curOpcode, primitives, refs, top-1, top-2))
                    {
                        pc = nextpc + bytecodeBuffer.getImmInt(pc);
                        top += BytecodeInstructions.getStackEffect(curOpcode);
                        continue loop;
                    }
                    break;

                case BEQ_S:
                case BGE_S:
                case BGT_S:
                case BLE_S:
                case BLT_S:
                    if(binaryCompareResult(curOpcode, primitives, refs, top-2, top-1))
                    {
                        pc = nextpc + bytecodeBuffer.getImmByte(pc);
                        top += BytecodeInstructions.getStackEffect(curOpcode);
                        continue loop;
                    }
                    break;

                case BRTRUE:
                case BRFALSE:
                    if(shouldBranch(curOpcode, primitives, refs, top-1))
                    {
                        pc = nextpc + bytecodeBuffer.getImmInt(pc);
                        top += BytecodeInstructions.getStackEffect(curOpcode);
                        continue loop;
                    }
                    break;

                case BRTRUE_S:
                case BRFALSE_S:
                    if(shouldBranch(curOpcode, primitives, refs, top-1))
                    {
                        pc = nextpc + bytecodeBuffer.getImmByte(pc);
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

                case AND:
                case OR:
                case XOR:
                    doIntegerBinary(curOpcode, primitives, refs, top-2, top-1); break;

                case CEQ:
                case CGT:
                case CLT:
                    doCompareBinary(curOpcode, primitives, refs, top-2, top-1); break;

                case SHL:
                case SHR:
                case SHR_UN:
                    doShiftBinary(curOpcode, primitives, refs, top-2, top-1); break;

                case LDFLD:
                case STFLD:
                case LDSFLD:
                case STSFLD:
                case LDFLDA:
                case LDSFLDA:
                    top = nodeizeOpFld(frame, primitives, refs, top, bytecodeBuffer.getImmToken(pc), pc, curOpcode); break;
                case CALL:
                case CALLVIRT:
                case NEWOBJ:
                    top = nodeizeOpToken(frame, primitives, refs, top, bytecodeBuffer.getImmToken(pc), pc, curOpcode); break;

                case BOX:
                    box(refs, primitives, top-1, method.getComponent().getType(bytecodeBuffer.getImmToken(pc))); break;




                case TRUFFLE_NODE:
                    top = nodes[bytecodeBuffer.getImmInt(pc)].execute(frame, primitives, refs); break;

                default:
                    CompilerDirectives.transferToInterpreterAndInvalidate();
                    throw new BACILInternalError(String.format("Unsuppored opcode %02x (%s) in %s", curOpcode, BytecodeInstructions.getName(curOpcode), method));
            }

            top += BytecodeInstructions.getStackEffect(curOpcode);
            pc = nextpc;
        }

    }

    private void putStr(long[] primitives, Object[] refs, int top, CLITablePtr immToken) {
        CLIUSHeapPtr ptr = new CLIUSHeapPtr(immToken.getRowNo());
        refs[top] = ptr.readString(method.getComponent().getUSHeap());
    }

    public static void box(Object[] refs, long[] primitives, int slot, Type type)
    {
        refs[slot] = type.stackToObject(refs[slot], primitives[slot]);
    }


    public static LocationReference getLocalReference(LocationsDescriptor descriptor, LocationsHolder holder, int index)
    {
        return new LocationReference(holder, descriptor.getOffset(index));
    }

    @ExplodeLoop
    public static void initLocations(int argsCount, int varsCount, Type[] localTypes, CILMethod method, Object[] locals, Object[] args)
    {
        if(method.isInitLocals())
        {
            for(int i = 0; i < varsCount; i++)
            {
                locals[i] = localTypes[i].initialValue();
            }
        }

        if (argsCount >= 0) System.arraycopy(args, 0, locals, varsCount, argsCount);
    }

    @ExplodeLoop
    public static void loadArgs(LocationsDescriptor descriptor, LocationsHolder holder, int argsCount, int varsCount, Object[] args)
    {
        for(int i = 0; i < argsCount; i++)
        {
            descriptor.objectToLocation(holder, varsCount+i, args[i]);
        }
    }

    @ExplodeLoop
    public static Object[] prepareArgs(long[] primitives, Object[] refs, int top, BACILMethod method, int skip)
    {

        final int argsCount = method.getArgsCount();
        final int varsCount = method.getVarsCount();
        final Object[] args = new Object[argsCount];
        final int firstArg = top - argsCount;
        final Type[] targetTypes = method.getLocationsTypes();

        CompilerAsserts.partialEvaluationConstant(argsCount);


        for(int i = skip; i < argsCount; i++)
        {
            args[i] = targetTypes[varsCount+i].stackToObject(refs[firstArg+i], primitives[firstArg+i]);
        }

        return args;
    }

    private int addNode(CallableNode node)
    {
        CompilerAsserts.neverPartOfCompilation();
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


    public int nodeizeOpToken(VirtualFrame frame, long[] primitives, Object[] refs, int top, CLITablePtr token, int pc, int opcode)
    {

        CompilerDirectives.transferToInterpreterAndInvalidate(); // because we are about to change something that is compilation final

        final CallableNode node;
        switch (opcode)
        {
            case CALL:
            case CALLVIRT: //TODO HACK should actually call virtually!!
                node = new CallNode(method.getComponent().getMethod(token, lookupContextReference(BACILLanguage.class).get()), top);
                break;
            case NEWOBJ:
                node = new ConstructorNode(method.getComponent().getMethod(token, lookupContextReference(BACILLanguage.class).get()), top);
                break;
            default:
                CompilerAsserts.neverPartOfCompilation();
                throw new BACILInternalError(String.format("Can't nodeize opcode %02x (%s) yet.", opcode, BytecodeInstructions.getName(opcode)));
        }
        int index = addNode(node);
        byte[] patch = preparePatch((byte)TRUFFLE_NODE, index, BytecodeInstructions.getLength(opcode));
        bytecodeBuffer.patchBytecode(pc, patch);

        return nodes[index].execute(frame, primitives, refs);

    }

    public int nodeizeOpFld(VirtualFrame frame, long[] primitives, Object[] refs, int top, CLITablePtr token, int pc, int opcode)
    {
        CompilerDirectives.transferToInterpreterAndInvalidate(); // because we are about to change something that is compilation final
        Type definingType;
        if(token.getTableId() == CLITableConstants.CLI_TABLE_MEMBER_REF)
        {
            CLIMemberRefTableRow row = method.getComponent().getTableHeads().getMemberRefTableHead().skip(token);
            definingType = method.getComponent().getType(row.getKlass());
        } else if (token.getTableId() == CLITableConstants.CLI_TABLE_FIELD)
        {
            definingType = method.getComponent().findDefiningType(method.getComponent().getTableHeads().getFieldTableHead().skip(token));
        } else {
            throw new BACILInternalError("Invalid token type.");
        }

        definingType.init();


        final CallableNode node;
        switch (opcode)
        {
            case STFLD:
                node = new StfldNode(token, method.getComponent(), top, definingType);
                break;
            case LDFLD:
                node = new LdfldNode(token, method.getComponent(), top, definingType);
                break;
            case LDSFLD:
                node = new LdsfldNode(token, method.getComponent(), top, definingType);
                break;
            case STSFLD:
                node = new StsfldNode(token, method.getComponent(), top, definingType);
                break;
            case LDFLDA:
                node = new LdfldaNode(token, method.getComponent(), top, definingType);
                break;
            case LDSFLDA:
                node = new LdsfldaNode(token, method.getComponent(), top, definingType);
                break;
            default:
                CompilerAsserts.neverPartOfCompilation();
                throw new BACILInternalError(String.format("Can't nodeize opcode %02x (%s) yet.", opcode, BytecodeInstructions.getName(opcode)));
        }
        int index = addNode(node);
        byte[] patch = preparePatch((byte)TRUFFLE_NODE, index, BytecodeInstructions.getLength(opcode));
        bytecodeBuffer.patchBytecode(pc, patch);

        return nodes[index].execute(frame, primitives, refs);

    }



    public static boolean shouldBranch(int opcode, long[] primitives, Object[] refs, int slot)
    {
        boolean value;
        if(ExecutionStackPrimitiveMarker.isExecutionStackPrimitiveMarker(refs[slot]))
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

    public static boolean binaryCompareResult(int opcode, long[] primitives, Object[] refs, int slot1, int slot2)
    {
        if(ExecutionStackPrimitiveMarker.isExecutionStackPrimitiveMarker(refs[slot1]) && ExecutionStackPrimitiveMarker.isExecutionStackPrimitiveMarker(refs[slot2]))
        {
            //using the numeric binary table here as it seems to be the same so far
            ExecutionStackPrimitiveMarker resultType = binaryNumericResultTypes[((ExecutionStackPrimitiveMarker)refs[slot1]).getTag()][((ExecutionStackPrimitiveMarker)refs[slot2]).getTag()];
            if(resultType == null)
            {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                throw new BACILInternalError("Invalid types for comparison");
            }

            boolean result = false;

            switch(opcode)
            {
                case CGT:
                case BGT:
                case BGT_S:
                    result = primitives[slot1] > primitives[slot2];
                    break;
                case BGE:
                case BGE_S:
                    result = primitives[slot1] >= primitives[slot2];
                    break;

                case CLT:
                case BLT:
                case BLT_S:
                    result = primitives[slot1] < primitives[slot2];
                    break;
                case BLE:
                case BLE_S:
                    result = primitives[slot1] <= primitives[slot2];
                    break;

                case CEQ:
                case BEQ:
                case BEQ_S:
                    result = primitives[slot1] == primitives[slot2];
                    break;


            }

            return result;

        } else {
            switch(opcode)
            {
                case CEQ:
                case BEQ:
                case BEQ_S:
                    return refs[slot1] == refs[slot2];
            }

            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("Unimplemented.");
        }
    }

    public static void doCompareBinary(int opcode, long[] primitives, Object[] refs, int slot1, int slot2)
    {
        //TODO floaty!
        boolean result = binaryCompareResult(opcode, primitives, refs, slot1, slot2);
        primitives[slot1] = result ? 1 : 0;
        refs[slot1] = ExecutionStackPrimitiveMarker.EXECUTION_STACK_INT32;

    }

    public static void doIntegerBinary(int opcode, long[] primitives, Object[] refs, int slot1, int slot2)
    {
        //TODO floaty!
        if(ExecutionStackPrimitiveMarker.isExecutionStackPrimitiveMarker(refs[slot1]) && ExecutionStackPrimitiveMarker.isExecutionStackPrimitiveMarker(refs[slot2]))
        {
            ExecutionStackPrimitiveMarker resultType = binaryIntegerResultTypes[((ExecutionStackPrimitiveMarker)refs[slot1]).getTag()][((ExecutionStackPrimitiveMarker)refs[slot2]).getTag()];
            if(resultType == null)
            {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                throw new BACILInternalError("These types can't be args of integer binary");
            }
            long result = 0;
            switch(opcode)
            {
                case AND:
                    result = primitives[slot1] & primitives[slot2];
                    break;
                case OR:
                    result = primitives[slot1] | primitives[slot2];
                    break;
                case XOR:
                    result = primitives[slot1] ^ primitives[slot2];
                    break;

            }

            if(resultType == ExecutionStackPrimitiveMarker.EXECUTION_STACK_INT32)
            {
                result = TypeHelpers.truncate32(result);
            }

            primitives[slot1] = result;
            refs[slot1] = resultType;

        }  else {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("Unimplemented.");
        }
    }

    public static void doNumericBinary(int opcode, long[] primitives, Object[] refs, int slot1, int slot2)
    {
        //TODO floaty!
        if(ExecutionStackPrimitiveMarker.isExecutionStackPrimitiveMarker(refs[slot1]) && ExecutionStackPrimitiveMarker.isExecutionStackPrimitiveMarker(refs[slot2]))
        {
            ExecutionStackPrimitiveMarker resultType = binaryNumericResultTypes[((ExecutionStackPrimitiveMarker)refs[slot1]).getTag()][((ExecutionStackPrimitiveMarker)refs[slot2]).getTag()];
            if(resultType == null)
            {
                CompilerDirectives.transferToInterpreterAndInvalidate();
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

            if(resultType == ExecutionStackPrimitiveMarker.EXECUTION_STACK_INT32)
            {
                result = TypeHelpers.truncate32(result);
            }

            primitives[slot1] = result;
            refs[slot1] = resultType;

        }  else {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("Unimplemented.");
        }
    }

    public static void doShiftBinary(int opcode, long[] primitives, Object[] refs, int slot1, int slot2)
    {
        if(refs[slot2] != ExecutionStackPrimitiveMarker.EXECUTION_STACK_INT32 &&
        refs[slot2] != ExecutionStackPrimitiveMarker.EXECUTION_STACK_NATIVE_INT)
        {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("Invalid shift value.");

        }

        switch(opcode)
        {
            case SHL:
                primitives[slot1] = primitives[slot1] << primitives[slot2]; break;
            case SHR_UN:
                primitives[slot1] = primitives[slot1] >>> primitives[slot2]; break;
            case SHR:
                if(refs[slot1] == ExecutionStackPrimitiveMarker.EXECUTION_STACK_INT32) primitives[slot1] = TypeHelpers.signExtend32(primitives[slot1]);
                primitives[slot1] = primitives[slot1] >> primitives[slot2];
                break;
        }

    }


    public static int doJmp(BytecodeBuffer bytecodeBuffer, int pc, int offset)
    {
        return bytecodeBuffer.nextInstruction(pc) + offset;
    }

    public static Object getReturnValue(long[] primitives, Object[] refs, int slot, Type retType)
    {
        if((retType instanceof SystemVoidType))
        {
            return null; //TODO polyglot API vyzaduje vraceni neceho chytreho
        }


        return retType.stackToObject(refs[slot], primitives[slot]);
    }

    public static void loadIndirect(long[] primitives, Object[] refs, int slot, LocationReference locationReference, Type type)
    {
        type.locationToStack(locationReference.getHolder(), locationReference.getHolderOffset(), refs, primitives, slot);
    }

    public static void storeIndirect(long primitive, Object ref, LocationReference locationReference, Type type)
    {
        type.stackToLocation(locationReference.getHolder(), locationReference.getHolderOffset(), ref, primitive);
    }

    public static void loadStack(long[] primitives, Object[] refs, int slot, LocationsDescriptor descriptor, LocationsHolder locals, int localSlot)
    {
        CompilerAsserts.partialEvaluationConstant(descriptor);
        descriptor.locationToStack(locals, localSlot, refs, primitives, slot);
    }

    public static void storeStack(long[] primitives, Object[] refs, int slot, LocationsDescriptor descriptor, LocationsHolder locals, int localSlot)
    {
        CompilerAsserts.partialEvaluationConstant(descriptor);
        descriptor.stackToLocation(locals, localSlot, refs[slot], primitives[slot]);
    }

    public static void putInt32(long[] primitives, Object[] refs, int slot, int value)
    {
        primitives[slot] = value;
        refs[slot] = ExecutionStackPrimitiveMarker.EXECUTION_STACK_INT32;
    }

    public static void putInt64(long[] primitives, Object[] refs, int slot, long value)
    {
        primitives[slot] = value;
        refs[slot] = ExecutionStackPrimitiveMarker.EXECUTION_STACK_INT64;
    }

    public CILMethod getMethod() {
        return method;
    }


}
