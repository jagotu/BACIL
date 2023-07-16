package com.vztekoverflow.cilostazol.nodes;

import static com.vztekoverflow.bacil.bytecode.BytecodeInstructions.LDARGA_S;
import static com.vztekoverflow.cil.parser.bytecode.BytecodeInstructions.*;

import com.oracle.truffle.api.HostCompilerDirectives;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.BytecodeOSRNode;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.vztekoverflow.cil.parser.bytecode.BytecodeBuffer;
import com.vztekoverflow.cil.parser.bytecode.BytecodeInstructions;
import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cilostazol.exceptions.InvalidCLIException;
import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;
import com.vztekoverflow.cilostazol.runtime.objectmodel.StaticObject;
import com.vztekoverflow.cilostazol.runtime.symbols.*;

import java.util.Arrays;

public class CILMethodNode extends CILNodeBase implements BytecodeOSRNode {
  private final MethodSymbol method;
  private final byte[] cil;
  private final BytecodeBuffer bytecodeBuffer;
  private final FrameDescriptor frameDescriptor;
  private final TypeSymbol[] taggedFrame;

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
        Arrays.stream(method.getParameters()).map(x -> x.getType()).toArray(TypeSymbol[]::new);
    int topStack = CILOSTAZOLFrame.getStartArgsOffset(getMethod());

    for (int i = 0; i < method.getParameters().length; i++) {
      switch (argTypes[i].getKind()) {
        case Boolean:
          CILOSTAZOLFrame.putInt(frame, topStack, (boolean) args[i + receiverSlot] ? 1 : 0);
          break;
        case Char:
          CILOSTAZOLFrame.putInt(frame, topStack, (byte) args[i + receiverSlot]);
          break;
        case Int:
          CILOSTAZOLFrame.putInt(frame, topStack, (int) args[i + receiverSlot]);
          break;
        case Long:
          CILOSTAZOLFrame.putLong(frame, topStack, (int) args[i + receiverSlot]);
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
      }

      topStack += BytecodeInstructions.getStackEffect(curOpcode);
      pc = nextpc;
    }
  }

  // region CIL opcode helpers
  private void loadValueOnTop(VirtualFrame frame, int top, int value) {
    // We want to tag the stack by types in it
    CILOSTAZOLFrame.putInt(frame, top, value);
    taggedFrame[top] =
        getMethod()
            .getContext()
            .getType("System", "Int32", AssemblyIdentity.SystemPrivateCoreLib());
  }

  private void loadValueOnTop(VirtualFrame frame, int top, long value) {
    // We want to tag the stack by types in it
    CILOSTAZOLFrame.putLong(frame, top, value);
    taggedFrame[top] =
        getMethod()
            .getContext()
            .getType("System", "Int64", AssemblyIdentity.SystemPrivateCoreLib());
  }

  private void loadValueOnTop(VirtualFrame frame, int top, double value) {
    // We want to tag the stack by types in it
    CILOSTAZOLFrame.putDouble(frame, top, value);
    taggedFrame[top] =
        getMethod()
            .getContext()
            .getType("System", "Double", AssemblyIdentity.SystemPrivateCoreLib());
  }

  private void loadValueOnTop(VirtualFrame frame, int top, float value) {
    // We want to tag the stack by types in it
    CILOSTAZOLFrame.putFloat(frame, top, value);
    taggedFrame[top] =
        getMethod()
            .getContext()
            .getType("System", "Single", AssemblyIdentity.SystemPrivateCoreLib());
  }

  private void storeValueToLocal(VirtualFrame frame, int localIdx, int top) {
    // Locals are already typed
    // TODO: type checking
    CILOSTAZOLFrame.Copy(frame, top, localIdx);
    // pop taggedFrame
    taggedFrame[top] = null;
  }

  private void loadLocalToTop(VirtualFrame frame, int localIdx, int top) {
    int localSlot = CILOSTAZOLFrame.getStartLocalsOffset(getMethod()) + localIdx;
    CILOSTAZOLFrame.Copy(frame, localSlot, top);
    // Tag the top of the stack
    taggedFrame[top] = taggedFrame[localSlot];
  }

  private void loadArgToTop(VirtualFrame frame, int argIdx, int top) {
    int argSlot = CILOSTAZOLFrame.getStartArgsOffset(getMethod()) + argIdx;
    CILOSTAZOLFrame.Copy(frame, argSlot, top);
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

  private void popStack(int top) {
    // pop taggedFrame
    taggedFrame[top] = null;
  }

  private Object getReturnValue(VirtualFrame frame, int top) {
    TypeSymbol retType = getMethod().getReturnType().getType();
    // TODO: type checking
    switch (retType.getKind()) {
      case Boolean -> {
        return CILOSTAZOLFrame.popInt(frame, top) > 1;
      }
      case Char -> {
        return (byte) CILOSTAZOLFrame.popInt(frame, top - 1);
      }
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
}
