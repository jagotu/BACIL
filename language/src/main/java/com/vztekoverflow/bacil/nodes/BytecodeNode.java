package com.vztekoverflow.bacil.nodes;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.HostCompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.bytecode.BytecodeBuffer;
import com.vztekoverflow.bacil.bytecode.BytecodeInstructions;
import com.vztekoverflow.bacil.nodes.instructions.*;
import com.vztekoverflow.bacil.parser.cil.CILMethod;
import com.vztekoverflow.bacil.parser.cli.tables.CLIComponentTablePtr;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.parser.cli.tables.CLIUSHeapPtr;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLIMemberRefTableRow;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITableConstants;
import com.vztekoverflow.bacil.runtime.BACILMethod;
import com.vztekoverflow.bacil.runtime.EvaluationStackPrimitiveMarker;
import com.vztekoverflow.bacil.runtime.LocationReference;
import com.vztekoverflow.bacil.runtime.SZArray;
import com.vztekoverflow.bacil.runtime.locations.LocationsDescriptor;
import com.vztekoverflow.bacil.runtime.locations.LocationsHolder;
import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.TypeHelpers;
import com.vztekoverflow.bacil.runtime.types.builtin.BuiltinTypes;
import com.vztekoverflow.bacil.runtime.types.builtin.SystemVoidType;

import java.util.Arrays;

import static com.vztekoverflow.bacil.bytecode.BytecodeInstructions.*;

/**
 * A Truffle node representing a {@link CILMethod} body.
 * Directly interprets simple instructions and nodeizes complex ones.
 *
 * The special TRUFFLE_NODE instruction is used to replace nodeized instructions.
 *
 * The evaluation stack is represented as two arrays, one for primitives ({@code long[]})
 * and one for references ({@code Object[]}.
 */
public class BytecodeNode extends Node {

    private final CILMethod method;
    private final BytecodeBuffer bytecodeBuffer;
    private final BuiltinTypes builtinTypes;

    //Nodeized instruction nodes are stored here
    @Children private EvaluationStackAwareNode[] nodes = new EvaluationStackAwareNode[0];

    /**
     * Create a new {@code BytecodeNode} for the specified {@link CILMethod}.
     * The method body is provided as a byte[].
     *
     * Stores arguments and variables in a single {@link LocationsHolder}, variables first and arguments last.
     * @param method the method this node represents
     * @param bytecode method's body bytes
     */
    public BytecodeNode(CILMethod method, byte[] bytecode)
    {
        this.method = method;
        this.bytecodeBuffer = new BytecodeBuffer(bytecode);
        this.builtinTypes = method.getComponent().getBuiltinTypes();
    }


    /**
     * Run the bytecode for this method.
     * @param frame the frame of the currently executing guest language method
     * @return return value of the method
     */
    @ExplodeLoop(kind = ExplodeLoop.LoopExplosionKind.MERGE_EXPLODE)
    @HostCompilerDirectives.BytecodeInterpreterSwitch
    public Object execute(VirtualFrame frame)
    {
        //As this is the most critical method for the performance of BACIL,
        //we use a lot of CompilerAsserts.partialEvaluationConstant to make sure
        //that this method is properly partially evaluated.
        //The MERGE_EXPLODE annotation is also vital for the performance,
        //so make sure that for a given pc, the state is always constant.

        //1. Make sure the method is called with the correct number of arguments
        Object[] args = frame.getArguments();
        if(args.length != method.getArgsCount())
        {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("Unexpected number of arguments!");
        }

        //2. Prepare the evaluation stack
        int evaluationStackCount = method.getMaxStack();
        CompilerAsserts.partialEvaluationConstant(evaluationStackCount);

        //We use two arrays to represent the evaluation stack, one for primitives and one for references.
        //For references, the ref is stored directly in refs[] and the slot in primitives[] is undefined.
        //For primitives, the refs[] slot is filled with EvaluationStackPrimitiveMarker objects
        //that allow tracking of the type (int64/int32/native int/F)
        // Long term TODO: change this to use the new Truffle frame indexed slots
        long[] primitives = new long[evaluationStackCount];
        Object[] refs = new Object[evaluationStackCount];

        //3. Prepare locations for arguments and local variables
        final int argsCount = method.getArgsCount();
        final int varsCount = method.getVarsCount();

        CompilerAsserts.partialEvaluationConstant(argsCount);
        CompilerAsserts.partialEvaluationConstant(varsCount);

        final LocationsDescriptor descriptor = method.getLocationDescriptor();
        final LocationsHolder locations = LocationsHolder.forDescriptor(descriptor);

        CompilerAsserts.partialEvaluationConstant(descriptor);


        //4. Fill the argument locations with values
        loadArgs(descriptor, locations, argsCount, varsCount, args);

        int top = 0; //stores the current stack top
        int pc = 0;  //stores the offset of current instruction (program counter)



        loop: while (true) {
            //5. Read opcode and nextpc
            int curOpcode = bytecodeBuffer.getOpcode(pc);
            int nextpc = bytecodeBuffer.nextInstruction(pc);

            //important asserts for merge_explode
            //
            //Partially evaluating the stack top is possible thanks to the following remark in
            //I.12.3.2.1 The evaluation stack:
            //The type state of the stack (the stack depth and types of each element on
            //the stack) at any given point in a program shall be identical for all possible control flow paths.
            //For example, a program that loops an unknown number of times and pushes a new element on
            //the stack at each iteration would be prohibited.
            CompilerAsserts.partialEvaluationConstant(pc);
            CompilerAsserts.partialEvaluationConstant(curOpcode);
            CompilerAsserts.partialEvaluationConstant(top);
            CompilerAsserts.partialEvaluationConstant(nextpc);

            //Print all executed instructions for debugging
            //System.out.printf("%s:%04x %s\n", method.getName(), pc, BytecodeInstructions.getName(curOpcode));

            //6. Execute the instruction based on the opcode
            switch(curOpcode) {
                case NOP:
                case POP:
                    break;

                case INITOBJ:
                    // > If typeTok is a value type, the initobj instruction initializes each field of dest to null
                    // > or a zero of the appropriate built-in type. [...] If typeTok is a reference
                    // > type, the initobj instruction has the same effect as ldnull followed by stind.ref
                    // As we are in Java and everything is always zero/null initialized, this is a nop for us
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
                case LDC_R4: putFloat(primitives, refs, top, Float.intBitsToFloat(bytecodeBuffer.getImmInt(pc))); break;
                case LDC_R8: putFloat(primitives, refs, top, Double.longBitsToDouble(bytecodeBuffer.getImmLong(pc))); break;




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

                case LDTOKEN:
                    refs[top] = new CLIComponentTablePtr(bytecodeBuffer.getImmToken(pc), method.getComponent()); break;

                case STARG_S:
                    storeStack(primitives, refs, top-1, descriptor, locations, varsCount + bytecodeBuffer.getImmUByte(pc)); break;


                case STIND_I1:
                case STIND_I2:
                case STIND_I4:
                case STIND_I8:
                case STIND_I:
                case STIND_R4:
                case STIND_R8:
                case STIND_REF:
                    storeIndirect(primitives[top-1], refs[top-1], (LocationReference) refs[top-2], builtinTypes.getForTypedOpcode(curOpcode)); break;

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
                    loadIndirect(primitives, refs, top-1, (LocationReference) refs[top-1], builtinTypes.getForTypedOpcode(curOpcode)); break;


                case LDELEM_I1:
                case LDELEM_U1:
                case LDELEM_I2:
                case LDELEM_U2:
                case LDELEM_I4:
                case LDELEM_U4:
                case LDELEM_I8:
                case LDELEM_I:
                case LDELEM_R4:
                case LDELEM_R8:
                case LDELEM_REF:
                    loadArrayElem(builtinTypes.getForTypedOpcode(curOpcode), primitives, refs, top); break;

                case STELEM_I1:
                case STELEM_I2:
                case STELEM_I4:
                case STELEM_I8:
                case STELEM_I:
                case STELEM_R4:
                case STELEM_R8:
                case STELEM_REF:
                    storeArrayElem(builtinTypes.getForTypedOpcode(curOpcode), primitives, refs, top); break;

                case LDELEM:
                case STELEM:
                case LDELEMA:
                    top = nodeizeOpArr(frame, primitives, refs, top, method.getComponent().getType(bytecodeBuffer.getImmToken(pc)), pc, curOpcode); break;

                case DUP:
                    refs[top]=refs[top-1];primitives[top]=primitives[top-1]; break;




                case RET:
                    return getReturnValue(primitives, refs, top-1, method.getRetType());

                case BR:
                    pc = nextpc + bytecodeBuffer.getImmInt(pc); continue loop;
                case BR_S:
                    pc = nextpc + bytecodeBuffer.getImmByte(pc);  continue loop;


                case BEQ:
                case BGE:
                case BGT:
                case BLE:
                case BLT:
                case BGE_UN:
                case BGT_UN:
                case BLE_UN:
                case BLT_UN:
                case BNE_UN:
                    // Long term TODO: this should use Truffle bytecode OSR support,
                    //  have a counter of backedges (loop jumps) and report that counter via LoopNode.reportLoopCount
                    //  once the function finishes. Also these if should be maybe profiled with ConditionProfile.createCountingProfile
                    //  (the implementation can be inlined to save the indirection)
                    if(binaryCompareResult(curOpcode, primitives, refs, top-2, top-1))
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
                case BGE_UN_S:
                case BGT_UN_S:
                case BLE_UN_S:
                case BLT_UN_S:
                case BNE_UN_S:
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

                case NEG:
                    doNegate(primitives, refs, top-1); break;

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

                case NOT:
                    doNot(primitives, refs, top-1); break;

                case CEQ:
                case CGT:
                case CLT:
                case CGT_UN:
                case CLT_UN:
                    doCompareBinary(curOpcode, primitives, refs, top-2, top-1); break;

                case SHL:
                case SHR:
                case SHR_UN:
                    doShiftBinary(curOpcode, primitives, refs, top-2, top-1); break;

                case CONV_I:
                case CONV_I1:
                case CONV_I2:
                case CONV_I4:
                case CONV_I8:
                case CONV_U:
                case CONV_U1:
                case CONV_U2:
                case CONV_U4:
                case CONV_U8:
                    doConvertToInt(curOpcode, primitives, refs, top-1); break;

                case CONV_R4:
                case CONV_R8:
                    doConvertToFloat(curOpcode, primitives, refs, top-1); break;

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
                case NEWARR:
                case BOX:
                case LDSTR:
                case UNBOX_ANY:
                    top = nodeizeOpToken(frame, primitives, refs, top, bytecodeBuffer.getImmToken(pc), pc, curOpcode); break;

                case LDLEN:
                    primitives[top-1] = TypeHelpers.truncate32(((SZArray)refs[top-1]).getLength());
                    refs[top-1] = EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32;
                    break;


                case TRUFFLE_NODE:
                    top = nodes[bytecodeBuffer.getImmInt(pc)].execute(frame, primitives, refs); break;

                default:
                    CompilerDirectives.transferToInterpreterAndInvalidate();
                    throw new BACILInternalError(String.format("Unsupported opcode %02x (%s) in %s (offset %x)", curOpcode, BytecodeInstructions.getName(curOpcode), method, pc));
            }

            //7. Set the next stack top and pc
            top += BytecodeInstructions.getStackEffect(curOpcode);
            pc = nextpc;
        }

    }

    /**
     * Get a managed reference (type {@code &}) to the specified location as a {@link LocationReference}
     * @param descriptor the {@link LocationsDescriptor} describing the location types
     * @param holder the {@link LocationsHolder} holding the location values
     * @param index index of the location to return a reference to
     * @return a managed reference (type {@code &}) to the specified location
     */
    private static LocationReference getLocalReference(LocationsDescriptor descriptor, LocationsHolder holder, int index)
    {
        return new LocationReference(holder, descriptor.getPrimitiveOffset(index), descriptor.getRefOffset(index), descriptor.getType(index));
    }


    /**
     * Store the provided argument values into the argument locations.
     * @param descriptor the {@link LocationsDescriptor} describing the location types
     * @param holder the {@link LocationsHolder} holding the location values
     * @param argsCount count of argument locations
     * @param varsCount count of variable locations
     * @param args the argument values
     */
    @ExplodeLoop
    private static void loadArgs(LocationsDescriptor descriptor, LocationsHolder holder, int argsCount, int varsCount, Object[] args)
    {
        for(int i = 0; i < argsCount; i++)
        {
            descriptor.objectToLocation(holder, varsCount+i, args[i]);
        }
    }

    /**
     * Prepare arguments for calling a method, taking them from the evaluation stack and putting them in an
     * Object[].
     * @param primitives primitives on the evaluation stack
     * @param refs references on the evaluation stack
     * @param top the current evaluation stack top
     * @param method the method to prepare the args for
     * @param skip number of locations (from the beginning) to ignore
     * @return an array of objects representing the arguments that can be used to call the target method
     */
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

    /**
     * Add an {@link EvaluationStackAwareNode} to the children of this node.
     * @param node the node to add
     * @return index of the added node
     */
    private int addNode(EvaluationStackAwareNode node)
    {
        CompilerAsserts.neverPartOfCompilation();
        nodes = Arrays.copyOf(nodes, nodes.length + 1);
        int nodeIndex = nodes.length - 1; // latest empty slot
        nodes[nodeIndex] = insert(node);
        return nodeIndex;
    }

    /**
     * Get a byte[] representing an instruction with the specified opcode and a 32-bit immediate value.
     * @param opcode opcode of the new instruction
     * @param imm 32-bit immediate value of the new instruction
     * @param targetLength the length of the resulting patch, instruction will be padded with NOPs
     * @return The new instruction bytes.
     */
    private static byte[] preparePatch(byte opcode, int imm, int targetLength)
    {
        assert(targetLength >= 5); //Smaller instructions won't fit the 32-bit immediate
        byte[] patch = new byte[targetLength];
        patch[0] = opcode;
        patch[1] = (byte)(imm & 0xFF);
        patch[2] = (byte)((imm >> 8) & 0xFF);
        patch[3] = (byte)((imm >> 16) & 0xFF);
        patch[4] = (byte)((imm >> 24) & 0xFF);
        return patch;
    }

    /**
     * Loads an array element to the evaluation stack.
     *
     * Stack transition: ..., array, index → ..., value
     * @param elementType the type of the element
     * @param primitives primitives on the evaluation stack
     * @param refs references on the evaluation stack
     * @param top current evaluation stack
     */
    public static void loadArrayElem(Type elementType, long[] primitives, Object[] refs, int top)
    {
        //Breaks standard: We should also support native int as the index here, but for us
        //native int is 64-bit, and Java arrays only use 32-bit indexers.
        if(refs[top-1] != EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32)
        {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("Only INT32 supported as SZArray index");
        }

        int index = (int)primitives[top-1];
        SZArray array = (SZArray) refs[top-2];
        // Only used for builtin types so no need to support valuetype structures
        elementType.locationToStack(array.getFieldsHolder(), index, index, refs, primitives, top-2);
    }


    /**
     * Stores a value from the evaluation stack to an array element.
     *
     * Stack transition: ..., array, index, value → ...
     * @param elementType the type of the element
     * @param primitives primitives on the evaluation stack
     * @param refs references on the evaluation stack
     * @param top current evaluation stack
     */
    public static void storeArrayElem(Type elementType, long[] primitives, Object[] refs, int top)
    {
        //Breaks standard: We should also support native int as the index here, but for us
        //native int is 64-bit, and Java arrays only use 32-bit indexers.
        if(refs[top-2] != EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32)
        {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("Only INT32 supported as SZArray index");
        }

        int index = (int)primitives[top-2];
        SZArray array = (SZArray) refs[top-3];
        // Only used for builtin types so no need to support valuetype structures
        elementType.stackToLocation(array.getFieldsHolder(), index, index, refs[top-1], primitives[top-1]);
    }

    /**
     * Nodeize an instruction with a generic token as an immediate parameter and execute it.
     * @param frame the frame of the currently executing guest language method
     * @param primitives primitives on the evaluation stack
     * @param refs references on the evaluation stack
     * @param top current evaluation stack
     * @param token the immediate token from the instruction
     * @param pc the offset of the instruction (program counter)
     * @param opcode the opcode of the instruction
     * @return stack top after executing the instruction
     */
    private int nodeizeOpToken(VirtualFrame frame, long[] primitives, Object[] refs, int top, CLITablePtr token, int pc, int opcode)
    {
        //because we are about to change the children[], which is compilation final,
        //we have to invalidate the previous state.
        CompilerDirectives.transferToInterpreterAndInvalidate();

        final EvaluationStackAwareNode node;
        //create a node for the instruction
        switch (opcode)
        {
            case CALL:
                node = new NonvirtualCallNode(method.getComponent().getMethod(token), top);
                break;
            case CALLVIRT:
                BACILMethod targetMethod = method.getComponent().getMethod(token);
                if(targetMethod.isVirtual())
                {
                    node = new VirtualCallNode(targetMethod, top);
                } else {
                    node = new NonvirtualCallNode(targetMethod, top);
                }
                break;
            case NEWOBJ:
                node = new NewobjNode(method.getComponent().getMethod(token), top);
                break;
            case NEWARR:
                node = new NewarrNode(method.getComponent().getType(token), top);
                break;
            case BOX:
                node = new BoxNode(method.getComponent().getType(token), top);
                break;
            case UNBOX_ANY:
                node = new UnboxAnyNode(method.getComponent().getType(token), top);
                break;
            case LDSTR:
                CLIUSHeapPtr ptr = new CLIUSHeapPtr(token.getRowNo());
                node = new LdStrNode(ptr.readString(method.getComponent().getUSHeap()), top);
                break;
            default:
                CompilerAsserts.neverPartOfCompilation();
                throw new BACILInternalError(String.format("Can't nodeize opcode %02x (%s) yet.", opcode, BytecodeInstructions.getName(opcode)));
        }

        //add the node to children, and patch the bytecode with a TRUFFLE_NODE instruction and the node offset
        int index = addNode(node);
        byte[] patch = preparePatch((byte)TRUFFLE_NODE, index, BytecodeInstructions.getLength(opcode));
        bytecodeBuffer.patchBytecode(pc, patch);

        //execute the new node
        return nodes[index].execute(frame, primitives, refs);

    }

    /**
     * Nodeize an instruction with an array element type as an immediate parameter and execute it.
     * @param frame the frame of the currently executing guest language method
     * @param primitives primitives on the evaluation stack
     * @param refs references on the evaluation stack
     * @param top current evaluation stack
     * @param type the element type resolved from the instruction
     * @param pc the offset of the instruction (program counter)
     * @param opcode the opcode of the instruction
     * @return stack top after executing the instruction
     */
    private int nodeizeOpArr(VirtualFrame frame, long[] primitives, Object[] refs, int top, Type type, int pc, int opcode)
    {
        //because we are about to change the children[], which is compilation final,
        //we have to invalidate the previous state.
        CompilerDirectives.transferToInterpreterAndInvalidate();

        final EvaluationStackAwareNode node;
        //create a node for the instruction
        switch (opcode)
        {
            case LDELEM:
                node = new LdelemNode(type, top);
                break;
            case STELEM:
                node = new StelemNode(type, top);
                break;
            case LDELEMA:
                node = new LdelemaNode(type, top);
                break;
            default:
                CompilerAsserts.neverPartOfCompilation();
                throw new BACILInternalError(String.format("Can't nodeize opcode %02x (%s) yet.", opcode, BytecodeInstructions.getName(opcode)));
        }

        //add the node to children, and patch the bytecode with a TRUFFLE_NODE instruction and the node offset
        int index = addNode(node);
        byte[] patch = preparePatch((byte)TRUFFLE_NODE, index, BytecodeInstructions.getLength(opcode));
        bytecodeBuffer.patchBytecode(pc, patch);

        //execute the new node
        return nodes[index].execute(frame, primitives, refs);
    }

    /**
     * Nodeize an instruction with a field token as an immediate parameter and execute it.
     * @param frame the frame of the currently executing guest language method
     * @param primitives primitives on the evaluation stack
     * @param refs references on the evaluation stack
     * @param top current evaluation stack
     * @param token the immediate field token from the instruction
     * @param pc the offset of the instruction (program counter)
     * @param opcode the opcode of the instruction
     * @return stack top after executing the instruction
     */
    private int nodeizeOpFld(VirtualFrame frame, long[] primitives, Object[] refs, int top, CLITablePtr token, int pc, int opcode)
    {
        //because we are about to change the children[], which is compilation final,
        //we have to invalidate the previous state.
        CompilerDirectives.transferToInterpreterAndInvalidate();

        //find the definingType
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

        //make sure the definingType is initialized
        definingType.init();

        final EvaluationStackAwareNode node;
        //create a node for the instruction
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

        //add the node to children, and patch the bytecode with a TRUFFLE_NODE instruction and the node offset
        int index = addNode(node);
        byte[] patch = preparePatch((byte)TRUFFLE_NODE, index, BytecodeInstructions.getLength(opcode));
        bytecodeBuffer.patchBytecode(pc, patch);

        //execute the new node
        return nodes[index].execute(frame, primitives, refs);

    }


    /**
     * Evaluate whether the branch should be taken for simple (true/false) conditional branch instructions
     * based on a value on the evaluation stack.
     * @param opcode the opcode of the instruction
     * @param primitives primitives on the evaluation stack
     * @param refs references on the evaluation stack
     * @param slot the slot the condition value is in
     * @return whether to take the branch or not
     */
    private static boolean shouldBranch(int opcode, long[] primitives, Object[] refs, int slot)
    {
        boolean value;
        if(EvaluationStackPrimitiveMarker.isEvaluationStackPrimitiveMarker(refs[slot]))
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

    /**
     * Do a binary comparison of values on the evaluation stack and return the result as a boolean.
     * @param opcode the opcode of the instruction
     * @param primitives primitives on the evaluation stack
     * @param refs references on the evaluation stack
     * @param slot1 the slot the first value is in
     * @param slot2 the slot the second value is in
     * @return the comparison result as a boolean
     */
    private static boolean binaryCompareResult(int opcode, long[] primitives, Object[] refs, int slot1, int slot2)
    {

        if(EvaluationStackPrimitiveMarker.isEvaluationStackPrimitiveMarker(refs[slot1]) && EvaluationStackPrimitiveMarker.isEvaluationStackPrimitiveMarker(refs[slot2]))
        {
            //comparing primitives
            EvaluationStackPrimitiveMarker resultType = binaryNumericResultTypes[((EvaluationStackPrimitiveMarker)refs[slot1]).getTag()][((EvaluationStackPrimitiveMarker)refs[slot2]).getTag()];
            if(resultType == null)
            {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                throw new BACILInternalError("Invalid types for comparison");
            }

            boolean result = false;

            if(resultType == EvaluationStackPrimitiveMarker.EVALUATION_STACK_F)
            {
                //comparing floats
                double arg1 = Double.longBitsToDouble(primitives[slot1]);
                double arg2 = Double.longBitsToDouble(primitives[slot2]);

                switch(opcode)
                {
                    //Breaks standard: we implement unordered and ordered double compares identically
                    case CGT:
                    case BGT:
                    case BGT_S:
                    case CGT_UN:
                    case BGT_UN:
                    case BGT_UN_S:
                        result = arg1 >  arg2;
                        break;
                    case BGE:
                    case BGE_S:
                    case BGE_UN:
                    case BGE_UN_S:
                        result = arg1 >= arg2;
                        break;

                    case CLT:
                    case BLT:
                    case BLT_S:
                    case CLT_UN:
                    case BLT_UN:
                    case BLT_UN_S:
                        result = arg1 <  arg2;
                        break;
                    case BLE:
                    case BLE_S:
                    case BLE_UN:
                    case BLE_UN_S:
                        result = arg1 <= arg2;
                        break;

                    case CEQ:
                    case BEQ:
                    case BEQ_S:
                        result = arg1 == arg2;
                        break;

                    case BNE_UN:
                    case BNE_UN_S:
                        result = arg1 != arg2;
                        break;

                }
            } else {
                //comparing integers
                long arg1 = primitives[slot1];
                long arg2 = primitives[slot2];
                if(resultType == EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32)
                {
                    //comparing 32-bit integers
                    switch(opcode)
                    {
                        case CGT:
                        case BGT:
                        case BGT_S:
                        case BGE:
                        case BGE_S:
                        case CLT:
                        case BLT:
                        case BLT_S:
                        case BLE:
                        case BLE_S:
                        case CEQ:
                        case BEQ:
                        case BEQ_S:
                            arg1 = TypeHelpers.signExtend32(arg1);
                            arg2 = TypeHelpers.signExtend32(arg2);
                            break;
                        case CGT_UN:
                        case BGT_UN:
                        case BGT_UN_S:
                        case BGE_UN:
                        case BGE_UN_S:
                        case CLT_UN:
                        case BLT_UN:
                        case BLT_UN_S:
                        case BLE_UN:
                        case BLE_UN_S:
                            arg1 = TypeHelpers.zeroExtend32(arg1);
                            arg2 = TypeHelpers.zeroExtend32(arg2);
                            break;
                    }

                }
                switch(opcode)
                {
                    //comparing 64-bit integers
                    case CGT:
                    case BGT:
                    case BGT_S:
                        result = arg1 >  arg2;
                        break;

                    case BGE:
                    case BGE_S:
                        result = arg1 >= arg2;
                        break;

                    case CLT:
                    case BLT:
                    case BLT_S:
                        result = arg1 <  arg2;
                        break;
                    case BLE:
                    case BLE_S:
                        result = arg1 <= arg2;
                        break;

                    case CEQ:
                    case BEQ:
                    case BEQ_S:
                        result = arg1 == arg2;
                        break;

                    case CGT_UN:
                    case BGT_UN:
                    case BGT_UN_S:
                        result = Long.compareUnsigned(arg1, arg2) > 0;
                        break;

                    case BGE_UN:
                    case BGE_UN_S:
                        result = Long.compareUnsigned(arg1, arg2) >= 0;
                        break;

                    case CLT_UN:
                    case BLT_UN:
                    case BLT_UN_S:
                        result = Long.compareUnsigned(arg1, arg2) < 0;
                        break;
                    case BLE_UN:
                    case BLE_UN_S:
                        result = Long.compareUnsigned(arg1, arg2) <= 0;
                        break;

                    case BNE_UN:
                    case BNE_UN_S:
                        result = arg1 != arg2;
                        break;



                }



            }

            return result;

        } else {
            //comparing references
            switch(opcode)
            {
                case CEQ:
                case BEQ:
                case BEQ_S:
                    return refs[slot1] == refs[slot2];

                case BNE_UN:
                case BNE_UN_S:
                    return refs[slot1] != refs[slot2];
            }

            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("Unimplemented.");
        }
    }




    /**
     * Do a binary comparison of values on the evaluation stack and put the result on the evaluation stack.
     * @param opcode the opcode of the instruction
     * @param primitives primitives on the evaluation stack
     * @param refs references on the evaluation stack
     * @param slot1 the slot the first value is in
     * @param slot2 the slot the second value is in
     */
    private static void doCompareBinary(int opcode, long[] primitives, Object[] refs, int slot1, int slot2)
    {
        boolean result = binaryCompareResult(opcode, primitives, refs, slot1, slot2);
        primitives[slot1] = result ? 1 : 0;
        refs[slot1] = EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32;

    }

    /**
     * Do a binary integer operation (and, or, xor) on values on the evaluation stack and put the result on the evaluation stack.
     * @param opcode the opcode of the instruction
     * @param primitives primitives on the evaluation stack
     * @param refs references on the evaluation stack
     * @param slot1 the slot the first value is in
     * @param slot2 the slot the second value is in
     */
    private static void doIntegerBinary(int opcode, long[] primitives, Object[] refs, int slot1, int slot2)
    {
        if(EvaluationStackPrimitiveMarker.isEvaluationStackPrimitiveMarker(refs[slot1]) && EvaluationStackPrimitiveMarker.isEvaluationStackPrimitiveMarker(refs[slot2]))
        {
            //calculating with primitives
            EvaluationStackPrimitiveMarker resultType = binaryIntegerResultTypes[((EvaluationStackPrimitiveMarker)refs[slot1]).getTag()][((EvaluationStackPrimitiveMarker)refs[slot2]).getTag()];
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

            if(resultType == EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32)
            {
                result = TypeHelpers.truncate32(result);
            }

            primitives[slot1] = result;
            refs[slot1] = resultType;

        }  else {
            //calculating with references
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("Unimplemented.");
        }
    }

    /**
     * Negate a value on the evaluation stack.
     * @param primitives primitives on the evaluation stack
     * @param refs references on the evaluation stack
     * @param slot the slot the value is in
     */
    private static void doNegate(long[] primitives, Object[] refs, int slot)
    {
        if(refs[slot] == EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32)
        {
            primitives[slot] = TypeHelpers.truncate32(-(int)primitives[slot]);
        } else if (refs[slot] == EvaluationStackPrimitiveMarker.EVALUATION_STACK_F)
        {
            primitives[slot] = Double.doubleToLongBits(-Double.longBitsToDouble(primitives[slot]));
        } else { //INT64, NATIVE INT
            primitives[slot] = -primitives[slot];
        }
    }

    /**
     * Bitwise complement an integer on the evaluation stack.
     * @param primitives primitives on the evaluation stack
     * @param refs references on the evaluation stack
     * @param slot the slot the value is in
     */
    private static void doNot(long[] primitives, Object[] refs, int slot)
    {
        if(refs[slot] == EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32)
        {
            primitives[slot] = TypeHelpers.truncate32(~(int)primitives[slot]);
        } else if (refs[slot] == EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT64)
        {
            primitives[slot] = ~primitives[slot];
        } else { //float, ref
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("not instruction called on invalid object: " + refs[slot].toString());
        }
    }

    /**
     * Do a binary numeric operation (add, sub, mul, div, rem) on values on the evaluation stack and put the result on the evaluation stack.
     * @param opcode the opcode of the instruction
     * @param primitives primitives on the evaluation stack
     * @param refs references on the evaluation stack
     * @param slot1 the slot the first value is in
     * @param slot2 the slot the second value is in
     */
    private static void doNumericBinary(int opcode, long[] primitives, Object[] refs, int slot1, int slot2)
    {
        if(EvaluationStackPrimitiveMarker.isEvaluationStackPrimitiveMarker(refs[slot1]) && EvaluationStackPrimitiveMarker.isEvaluationStackPrimitiveMarker(refs[slot2]))
        {
            //calculate with primitives
            EvaluationStackPrimitiveMarker resultType = binaryNumericResultTypes[((EvaluationStackPrimitiveMarker)refs[slot1]).getTag()][((EvaluationStackPrimitiveMarker)refs[slot2]).getTag()];
            if(resultType == null)
            {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                throw new BACILInternalError("These types can't be args of numeric binary");
            }

            long result = 0;

            if(resultType == EvaluationStackPrimitiveMarker.EVALUATION_STACK_F)
            {
                //calculate with floats
                double arg1 = Double.longBitsToDouble(primitives[slot1]);
                double arg2 = Double.longBitsToDouble(primitives[slot2]);
                double dblresult = 0;
                switch(opcode)
                {
                    case ADD:
                        dblresult = arg1 + arg2;
                        break;
                    case SUB:
                        dblresult = arg1 - arg2;
                        break;
                    case MUL:
                        dblresult = arg1 * arg2;
                        break;
                    case DIV:
                        dblresult = arg1 / arg2;
                        break;
                    case REM:
                        dblresult = arg1 % arg2;
                        break;

                }
                result = Double.doubleToLongBits(dblresult);
            } else {
                //calculate with integers
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
            }


            if(resultType == EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32)
            {
                result = TypeHelpers.truncate32(result);
            }

            primitives[slot1] = result;
            refs[slot1] = resultType;

        }  else {
            //calculate with references
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("Unimplemented.");
        }
    }

    /**
     * Convert a value on evaluation stack to a floating point value.
     * @param opcode the opcode of the instruction
     * @param primitives primitives on the evaluation stack
     * @param refs references on the evaluation stack
     * @param slot the slot the value is in
     */
    private static void doConvertToFloat(int opcode, long[] primitives, Object[] refs, int slot)
    {
        if(refs[slot] == EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32)
        {
            primitives[slot] = Double.doubleToLongBits((int)(primitives[slot]));
        } else if (refs[slot] == EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT64) {
            primitives[slot] = Double.doubleToLongBits(primitives[slot]);
        }

        if(opcode == CONV_R4)
        {
            primitives[slot] = Double.doubleToLongBits((float)Double.longBitsToDouble(primitives[slot]));
        }
        refs[slot] = EvaluationStackPrimitiveMarker.EVALUATION_STACK_F;
    }

    /**
     * Convert a value on evaluation stack to an integer value.
     * @param opcode the opcode of the instruction
     * @param primitives primitives on the evaluation stack
     * @param refs references on the evaluation stack
     * @param slot the slot the value is in
     */
    private static void doConvertToInt(int opcode, long[] primitives, Object[] refs, int slot)
    {
        long value = primitives[slot];
        if(refs[slot] == EvaluationStackPrimitiveMarker.EVALUATION_STACK_F)
        {
            //if source value is float, first convert to integer
            value = (long)Double.longBitsToDouble(primitives[slot]);
        }

        if(refs[slot] == EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32)
        {
            //if source value is 32-bit, sign extend to 64-bit
            switch(opcode) {
                case CONV_I1:
                case CONV_I2:
                case CONV_I4:
                case CONV_I8:
                case CONV_I:
                    value = TypeHelpers.signExtend32(value);
                case CONV_U1:
                case CONV_U2:
                case CONV_U4:
                case CONV_U8:
                case CONV_U:
                    value = TypeHelpers.zeroExtend32(value);
            }
        }
        switch(opcode)
        {
            case CONV_I1:
                primitives[slot] = TypeHelpers.signExtend8to32(value);
                refs[slot] = EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32;
                break;

            case CONV_I2:
                primitives[slot] = TypeHelpers.signExtend16to32(value);
                refs[slot] = EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32;
                break;

            case CONV_I4:
                primitives[slot] = TypeHelpers.truncate32(value);
                refs[slot] = EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32;
                break;

            case CONV_U1:
                primitives[slot] = TypeHelpers.zeroExtend8(value);
                refs[slot] = EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32;
                break;

            case CONV_U2:
                primitives[slot] = TypeHelpers.zeroExtend16(value);
                refs[slot] = EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32;
                break;

            case CONV_U4:
                primitives[slot] = TypeHelpers.zeroExtend32(value);
                refs[slot] = EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32;
                break;

            case CONV_U8:
            case CONV_I8:
                refs[slot] = EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT64;
                primitives[slot] = value;
                break;

            case CONV_U:
            case CONV_I:
                refs[slot] = EvaluationStackPrimitiveMarker.EVALUATION_STACK_NATIVE_INT;
                primitives[slot] = value;

            default:
                CompilerDirectives.transferToInterpreterAndInvalidate();
                throw new BACILInternalError("Invalid opcode for doConvertToInt.");
        }
    }

    /**
     * Do a numeric shift operation using values on the evaluation stack and put the result on the evaluation stack.
     * @param opcode the opcode of the instruction
     * @param primitives primitives on the evaluation stack
     * @param refs references on the evaluation stack
     * @param slot1 the slot the first value is in
     * @param slot2 the slot the second value is in
     */
    private static void doShiftBinary(int opcode, long[] primitives, Object[] refs, int slot1, int slot2)
    {
        if(refs[slot2] != EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32 &&
        refs[slot2] != EvaluationStackPrimitiveMarker.EVALUATION_STACK_NATIVE_INT)
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
                if(refs[slot1] == EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32) primitives[slot1] = TypeHelpers.signExtend32(primitives[slot1]);
                primitives[slot1] = primitives[slot1] >> primitives[slot2];
                break;
        }

    }

    /**
     * Get the return value from the evaluation stack as an object.
     * @param primitives primitives on the evaluation stack
     * @param refs references on the evaluation stack
     * @param slot the slot the return value is in
     * @param retType the type of the return value
     * @return the object representing the return value
     */
    private static Object getReturnValue(long[] primitives, Object[] refs, int slot, Type retType)
    {
        if((retType instanceof SystemVoidType))
        {
            //TODO polyglot API wants us to return something better, like a language-specific null value
            return null;
        }

        return retType.stackToObject(refs[slot], primitives[slot]);
    }


    /**
     * Implements an indirect load from a managed pointer represented by a {@link LocationReference}.
     * @param primitives primitives on the evaluation stack
     * @param refs references on the evaluation stack
     * @param slot the slot to put the result in
     * @param locationReference the managed pointer to load form
     * @param type expected type of the value
     */
    private static void loadIndirect(long[] primitives, Object[] refs, int slot, LocationReference locationReference, Type type)
    {
        //type safety check should be here, comparing compatibility of the reference type and the type from the instruction
        type.locationToStack(locationReference.getHolder(), locationReference.getPrimitiveOffset(), locationReference.getRefOffset(), refs, primitives, slot);
    }

    /**
     * Implements an indirect store to a managed pointer represented by a {@link LocationReference}.
     * @param primitive the primitive to store
     * @param ref the reference to store
     * @param locationReference the managed pointer to store to
     * @param type expected type of the value
     */
    private static void storeIndirect(long primitive, Object ref, LocationReference locationReference, Type type)
    {
        //type safety check should be here, comparing compatibility of the reference type and the type from the instruction
        type.stackToLocation(locationReference.getHolder(), locationReference.getPrimitiveOffset(), locationReference.getRefOffset(), ref, primitive);
    }

    /**
     * Load a value from a location to the evaluation stack.
     * @param primitives primitives on the evaluation stack
     * @param refs references on the evaluation stack
     * @param slot evaluation stack slot to load the value to
     * @param descriptor the {@link LocationsDescriptor} describing the location types
     * @param holder the {@link LocationsHolder} holding the location values
     * @param locationIndex the index of the location to load from
     */
    private static void loadStack(long[] primitives, Object[] refs, int slot, LocationsDescriptor descriptor, LocationsHolder holder, int locationIndex)
    {
        CompilerAsserts.partialEvaluationConstant(descriptor);
        descriptor.locationToStack(holder, locationIndex, refs, primitives, slot);
    }

    /**
     * Stores a value to a location from the evaluation stack.
     * @param primitives primitives on the evaluation stack
     * @param refs references on the evaluation stack
     * @param slot evaluation stack slot to store the value from
     * @param descriptor the {@link LocationsDescriptor} describing the location types
     * @param holder the {@link LocationsHolder} holding the location values
     * @param locationIndex the index of the location to store to
     */
    private static void storeStack(long[] primitives, Object[] refs, int slot, LocationsDescriptor descriptor, LocationsHolder holder, int locationIndex)
    {
        CompilerAsserts.partialEvaluationConstant(descriptor);
        descriptor.stackToLocation(holder, locationIndex, refs[slot], primitives[slot]);
    }


    /**
     * Load an int-32 constant on the evaluation stock
     * @param primitives primitives on the evaluation stack
     * @param refs references on the evaluation stack
     * @param slot evaluation stack slot to load the value to
     * @param value the constant value
     */
    private static void putInt32(long[] primitives, Object[] refs, int slot, int value)
    {
        primitives[slot] = TypeHelpers.truncate32(value);
        refs[slot] = EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32;
    }

    /**
     * Load an int-64 constant on the evaluation stock
     * @param primitives primitives on the evaluation stack
     * @param refs references on the evaluation stack
     * @param slot evaluation stack slot to load the value to
     * @param value the constant value
     */
    private static void putInt64(long[] primitives, Object[] refs, int slot, long value)
    {
        primitives[slot] = value;
        refs[slot] = EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT64;
    }

    /**
     * Load a float constant on the evaluation stock
     * @param primitives primitives on the evaluation stack
     * @param refs references on the evaluation stack
     * @param slot evaluation stack slot to load the value to
     * @param value the constant value
     */
    private static void putFloat(long[] primitives, Object[] refs, int slot, double value)
    {
        primitives[slot] = Double.doubleToLongBits(value);
        refs[slot] = EvaluationStackPrimitiveMarker.EVALUATION_STACK_F;
    }

    /**
     * Get the method whose body this BytecodeNode implements.
     * @return the method this BytecodeNode implements
     */
    public CILMethod getMethod() {
        return method;
    }


}
