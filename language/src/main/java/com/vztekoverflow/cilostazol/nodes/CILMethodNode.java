package com.vztekoverflow.cilostazol.nodes;

import static com.vztekoverflow.cil.parser.bytecode.BytecodeInstructions.*;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.HostCompilerDirectives;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.BytecodeOSRNode;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.vztekoverflow.cil.parser.bytecode.BytecodeBuffer;
import com.vztekoverflow.cil.parser.bytecode.BytecodeInstructions;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.CLIUSHeapPtr;
import com.vztekoverflow.cilostazol.exceptions.InterpreterException;
import com.vztekoverflow.cilostazol.exceptions.InvalidCLIException;
import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;
import com.vztekoverflow.cilostazol.nodes.nodeized.LDSTRNode;
import com.vztekoverflow.cilostazol.nodes.nodeized.NodeizedNodeBase;
import com.vztekoverflow.cilostazol.runtime.context.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.objectmodel.StaticObject;
import com.vztekoverflow.cilostazol.runtime.symbols.*;
import java.util.Arrays;

public class CILMethodNode extends CILNodeBase implements BytecodeOSRNode {
  private final MethodSymbol method;
  private final byte[] cil;
  private final BytecodeBuffer bytecodeBuffer;
  private final FrameDescriptor frameDescriptor;
  private final TypeSymbol[] taggedFrame;

  @Children private NodeizedNodeBase[] nodes = new NodeizedNodeBase[0];

  private CILMethodNode(MethodSymbol method, byte[] cilCode) {
    this.method = method;
    cil = cilCode;
    frameDescriptor = CILOSTAZOLFrame.create(method.getLocals().length, method.getMaxStack());
    this.bytecodeBuffer = new BytecodeBuffer(cil);
    taggedFrame = new TypeSymbol[frameDescriptor.getNumberOfSlots()];
  }

  public static CILMethodNode create(MethodSymbol method, byte[] cilCode) {
    return new CILMethodNode(method, cilCode);
  }

  public MethodSymbol getMethod() {
    return method;
  }

  public FrameDescriptor getFrameDescriptor() {
    return frameDescriptor;
  }

  // region CILNodeBase
  @Override
  public Object execute(VirtualFrame frame) {
    initializeFrame(frame);
    return execute(frame, 0, CILOSTAZOLFrame.getStartStackOffset(method));
  }

  private void initializeFrame(VirtualFrame frame) {
    // Init arguments
    Object[] args = frame.getArguments();

    boolean hasReceiver =
        !getMethod().getMethodFlags().hasFlag(MethodSymbol.MethodFlags.Flag.STATIC);
    int receiverSlot = CILOSTAZOLFrame.getStartLocalsOffset(getMethod());
    if (hasReceiver) {
      throw new NotImplementedException();
    }

    TypeSymbol[] argTypes =
        Arrays.stream(method.getParameters())
            .map(ParameterSymbol::getType)
            .toArray(TypeSymbol[]::new);
    int topStack = CILOSTAZOLFrame.getStartArgsOffset(getMethod());

    for (int i = 0; i < method.getParameters().length; i++) {
      switch (argTypes[i].getStackTypeKind()) {
        case Int:
          CILOSTAZOLFrame.putInt(frame, topStack, (int) args[i + receiverSlot]);
          break;
        case Long:
          CILOSTAZOLFrame.putLong(frame, topStack, (long) args[i + receiverSlot]);
          break;
        case Double:
          CILOSTAZOLFrame.putDouble(frame, topStack, (double) args[i + receiverSlot]);
          break;
        case Float:
          CILOSTAZOLFrame.putFloat(frame, topStack, (float) args[i + receiverSlot]);
          break;
        case Object:
          CILOSTAZOLFrame.putObject(frame, topStack, (StaticObject) args[i + receiverSlot]);
          break;
        default:
          throw new NotImplementedException();
      }
      taggedFrame[topStack] = argTypes[i + receiverSlot];
      topStack--;
    }
  }
  // endregion

  // region OSR
  @Override
  public Object executeOSR(VirtualFrame osrFrame, int target, Object interpreterState) {
    throw new NotImplementedException();
  }

  @Override
  public Object getOSRMetadata() {
    throw new NotImplementedException();
  }

  @Override
  public void setOSRMetadata(Object osrMetadata) {
    throw new NotImplementedException();
  }
  // endregion

  @ExplodeLoop(kind = ExplodeLoop.LoopExplosionKind.MERGE_EXPLODE)
  @HostCompilerDirectives.BytecodeInterpreterSwitch
  private Object execute(VirtualFrame frame, int pc, int topStack) {

    while (true) {
      int curOpcode = bytecodeBuffer.getOpcode(pc);
      int nextpc = bytecodeBuffer.nextInstruction(pc);
      switch (curOpcode) {
        case NOP:
        case POP:
          popStack(topStack - 1);
          break;

          // Loading on top of the stack
        case LDNULL:
          loadNull(frame, topStack);
          break;
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
          loadValueOnTop(frame, topStack, curOpcode - LDC_I4_0);
          break;
        case LDC_I4_S:
          loadValueOnTop(frame, topStack, bytecodeBuffer.getImmByte(pc));
          break;
        case LDC_I4:
          loadValueOnTop(frame, topStack, bytecodeBuffer.getImmInt(pc));
          break;
        case LDC_I8:
          loadValueOnTop(frame, topStack, bytecodeBuffer.getImmLong(pc));
          break;
        case LDC_R4:
          loadValueOnTop(frame, topStack, Float.intBitsToFloat(bytecodeBuffer.getImmInt(pc)));
          break;
        case LDC_R8:
          loadValueOnTop(frame, topStack, Double.longBitsToDouble(bytecodeBuffer.getImmLong(pc)));
          break;
        case LDSTR:
          topStack = nodeizeOpToken(frame, topStack, bytecodeBuffer.getImmToken(pc), pc, curOpcode);
          break;

          // Storing to locals
        case STLOC_0:
        case STLOC_1:
        case STLOC_2:
        case STLOC_3:
          storeValueToLocal(frame, topStack - 1, topStack - 1);
          break;
        case STLOC_S:
          storeValueToLocal(frame, bytecodeBuffer.getImmUByte(pc), topStack - 1);
          break;

          // Loading locals to top
        case LDLOC_0:
        case LDLOC_1:
        case LDLOC_2:
        case LDLOC_3:
          loadLocalToTop(frame, curOpcode - LDLOC_0, topStack - 1);
          break;
        case LDLOC_S:
          loadLocalToTop(frame, bytecodeBuffer.getImmUByte(pc), topStack - 1);
          break;
        case LDLOCA_S:
          loadLocalRefToTop(frame, bytecodeBuffer.getImmUByte(pc), topStack);
          break;

          // Loading args to top
        case LDARG_0:
        case LDARG_1:
        case LDARG_2:
        case LDARG_3:
          loadArgToTop(frame, curOpcode - LDARG_0, topStack);
          break;
        case LDARG_S:
          loadArgToTop(frame, bytecodeBuffer.getImmUByte(pc), topStack);
          break;
        case LDARGA_S:
          loadArgRefToTop(frame, bytecodeBuffer.getImmUByte(pc), topStack);
          break;

        case RET:
          return getReturnValue(frame, topStack - 1);

        case CALL:
          System.out.println("CALLING");
          break;

        case TRUFFLE_NODE:
          topStack = nodes[bytecodeBuffer.getImmInt(pc)].execute(frame, taggedFrame);
          break;

        default:
          System.out.println("Opcode not implemented: " + curOpcode);
          break;
      }

      topStack += BytecodeInstructions.getStackEffect(curOpcode);
      pc = nextpc;
    }
  }

  // region Helpers
  private void loadValueOnTop(VirtualFrame frame, int top, int value) {
    // We want to tag the stack by types in it
    CILOSTAZOLFrame.putInt(frame, top, value);
    taggedFrame[top] =
        getMethod()
            .getContext()
            .getType(CILOSTAZOLContext.CILBuiltInType.Int32);
  }

  private void loadValueOnTop(VirtualFrame frame, int top, long value) {
    // We want to tag the stack by types in it
    CILOSTAZOLFrame.putLong(frame, top, value);
    taggedFrame[top] =
        getMethod()
            .getContext()
            .getType(CILOSTAZOLContext.CILBuiltInType.Int64);
  }

  private void loadValueOnTop(VirtualFrame frame, int top, double value) {
    // We want to tag the stack by types in it
    CILOSTAZOLFrame.putDouble(frame, top, value);
    taggedFrame[top] =
        getMethod()
            .getContext()
            .getType(CILOSTAZOLContext.CILBuiltInType.Double);
  }

  private void loadValueOnTop(VirtualFrame frame, int top, float value) {
    // We want to tag the stack by types in it
    CILOSTAZOLFrame.putFloat(frame, top, value);
    taggedFrame[top] =
        getMethod()
            .getContext()
            .getType(CILOSTAZOLContext.CILBuiltInType.Single);
  }

  private void loadLocalToTop(VirtualFrame frame, int localIdx, int top) {
    int localSlot = CILOSTAZOLFrame.getStartLocalsOffset(getMethod()) + localIdx;
    CILOSTAZOLFrame.copy(frame, localSlot, top);
    // Tag the top of the stack
    taggedFrame[top] = taggedFrame[localSlot];
  }

  private void loadArgToTop(VirtualFrame frame, int argIdx, int top) {
    int argSlot = CILOSTAZOLFrame.getStartArgsOffset(getMethod()) + argIdx;
    CILOSTAZOLFrame.copy(frame, argSlot, top);
    // Tag the top of the stack
    taggedFrame[top] = taggedFrame[argSlot];
  }

  private void loadLocalRefToTop(VirtualFrame frame, int localIdx, int top) {
    int localSlot = CILOSTAZOLFrame.getStartLocalsOffset(getMethod()) + localIdx;
    CILOSTAZOLFrame.putInt(frame, top, localSlot);
    taggedFrame[top] = new LocalReferenceSymbol(taggedFrame[localSlot]);
  }

  private void loadArgRefToTop(VirtualFrame frame, int argIdx, int top) {
    int argSlot = CILOSTAZOLFrame.getStartArgsOffset(getMethod()) + argIdx;
    CILOSTAZOLFrame.putInt(frame, top, argSlot);
    taggedFrame[top] = new ArgReferenceSymbol(taggedFrame[argSlot]);
  }

  private void loadNull(VirtualFrame frame, int top) {
    // In this situation we don't know the type of null yet -> it will be determined later
    CILOSTAZOLFrame.putObject(frame, top, StaticObject.NULL);
    taggedFrame[top] = new NullSymbol();
  }

  private void storeValueToLocal(VirtualFrame frame, int localIdx, int top) {
    // Locals are already typed
    // TODO: type checking
    CILOSTAZOLFrame.copy(frame, top, localIdx);
    // pop taggedFrame
    taggedFrame[top] = null;
  }

  private void popStack(int top) {
    // pop taggedFrame
    taggedFrame[top] = null;
  }

  private Object getReturnValue(VirtualFrame frame, int top) {
    TypeSymbol retType = getMethod().getReturnType().getType();
    // TODO: type checking
    switch (retType.getStackTypeKind()) {
      case Int -> {
        return CILOSTAZOLFrame.popInt(frame, top);
      }
      case Float -> {
        return CILOSTAZOLFrame.popFloat(frame, top);
      }
      case Long -> {
        return CILOSTAZOLFrame.popLong(frame, top);
      }
      case Double -> {
        return CILOSTAZOLFrame.popDouble(frame, top);
      }
      case Void -> {
        return null;
      }
      case Object -> {
        return CILOSTAZOLFrame.popObject(frame, top);
      }
      default -> {
        throw new InvalidCLIException();
      }
    }
  }
  // endregion

  //region Nodeization
  /**
   * Get a byte[] representing an instruction with the specified opcode and a 32-bit immediate
   * value.
   *
   * @param opcode opcode of the new instruction
   * @param imm 32-bit immediate value of the new instruction
   * @param targetLength the length of the resulting patch, instruction will be padded with NOPs
   * @return The new instruction bytes.
   */
  private static byte[] preparePatch(byte opcode, int imm, int targetLength) {
    assert (targetLength >= 5); // Smaller instructions won't fit the 32-bit immediate
    byte[] patch = new byte[targetLength];
    patch[0] = opcode;
    patch[1] = (byte) (imm & 0xFF);
    patch[2] = (byte) ((imm >> 8) & 0xFF);
    patch[3] = (byte) ((imm >> 16) & 0xFF);
    patch[4] = (byte) ((imm >> 24) & 0xFF);
    return patch;
  }

  private int nodeizeOpToken(VirtualFrame frame,
                             int top,
                             CLITablePtr token,
                             int pc,
                             int opcode)
  {
    CompilerDirectives.transferToInterpreterAndInvalidate();
    final NodeizedNodeBase node;
    if (opcode == LDSTR) {
      CLIUSHeapPtr ptr = new CLIUSHeapPtr(token.getRowNo());
      node = new LDSTRNode(ptr.readString(method.getModule().getDefiningFile().getUSHeap()), top, method.getContext().getType(CILOSTAZOLContext.CILBuiltInType.String));
    } else {
      CompilerAsserts.neverPartOfCompilation();
      throw new InterpreterException();
    }

    int index = addNode(node);

    byte[] patch = preparePatch((byte) TRUFFLE_NODE, index, com.vztekoverflow.bacil.bytecode.BytecodeInstructions.getLength(opcode));
    bytecodeBuffer.patchBytecode(pc, patch);

    // execute the new node
    return nodes[index].execute(frame, taggedFrame);
  }

  private int addNode(NodeizedNodeBase node) {
    CompilerAsserts.neverPartOfCompilation();
    nodes = Arrays.copyOf(nodes, nodes.length + 1);
    int nodeIndex = nodes.length - 1; // latest empty slot
    nodes[nodeIndex] = insert(node);
    return nodeIndex;
  }
  //endregion
}
