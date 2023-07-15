package com.vztekoverflow.cilostazol.nodes;

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
import com.vztekoverflow.cilostazol.runtime.symbols.MethodSymbol;
import com.vztekoverflow.cilostazol.runtime.symbols.TypeSymbol;
import java.util.Arrays;

import static com.vztekoverflow.cil.parser.bytecode.BytecodeInstructions.*;

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
    int receiverSlot = hasReceiver ? 1 : 0;
    if (hasReceiver) {
      throw new NotImplementedException();
    }

    TypeSymbol[] argTypes =
        Arrays.stream(method.getParameters()).map(x -> x.getType()).toArray(TypeSymbol[]::new);
    int topStack = CILOSTAZOLFrame.getStartStackOffset(method) - 1;

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

    loop:
    while (true) {
      int curOpcode = bytecodeBuffer.getOpcode(pc);
      int nextpc = bytecodeBuffer.nextInstruction(pc);
      switch (curOpcode) {
        case NOP:
          break;

        case LDC_I4_S:
          CILOSTAZOLFrame.putInt(frame, topStack, bytecodeBuffer.getImmByte(pc));
          taggedFrame[topStack] =
              getMethod()
                  .getContext()
                  .getType("System", "Int32", AssemblyIdentity.SystemPrivateCoreLib());
          break;

        case RET:
          return getReturnValue(frame, topStack - 1);
      }

      topStack += BytecodeInstructions.getStackEffect(curOpcode);
      pc = nextpc;
    }
  }

  // region CIL opcode helpers
  private Object getReturnValue(VirtualFrame frame, int top) {
    TypeSymbol retType = getMethod().getReturnType().getType();

    switch (retType.getKind()) {
      case Boolean -> {
        return CILOSTAZOLFrame.popInt(frame, top - 1) > 1 ? true : false;
      }
      case Char -> {
        return (byte) CILOSTAZOLFrame.popInt(frame, top - 1);
      }
      case Int -> {
        return CILOSTAZOLFrame.popInt(frame, top - 1);
      }
      case Float -> {
        return CILOSTAZOLFrame.popFloat(frame, top - 1);
      }
      case Long -> {
        return CILOSTAZOLFrame.popLong(frame, top - 1);
      }
      case Double -> {
        return CILOSTAZOLFrame.popDouble(frame, top - 1);
      }
      case Void -> {
        return null;
      }
      case Object -> {
        return CILOSTAZOLFrame.popObject(frame, top - 1);
      }
      default -> {
        throw new InvalidCLIException();
      }
    }
  }
  // endregion
}
