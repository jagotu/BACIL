package com.vztekoverflow.cilostazol.nodes;

import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.IMethod;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public class CILOSTAZOLFrame {
  public static FrameDescriptor create(int locals, int stack) {
    int slotCount = locals + stack;
    FrameDescriptor.Builder builder = FrameDescriptor.newBuilder(slotCount);
    builder.addSlots(slotCount, FrameSlotKind.Static);
    return builder.build();
  }

  public static int getStartStackOffset(IMethod method) {
    return method.getMaxStack();
  }

  public static CILOSTAZOLFrameSlotKind getKind(IType type) {
    if (type.getNamespace() == "System" && type.getName() == "Boolean") {
      return CILOSTAZOLFrameSlotKind.Boolean;
    } else if (type.getNamespace() == "System" && type.getName() == "Byte") {
      return CILOSTAZOLFrameSlotKind.Byte;
    }
    if (type.getNamespace() == "System" && type.getName() == "Int") {
      return CILOSTAZOLFrameSlotKind.Int;
    }
    if (type.getNamespace() == "System" && type.getName() == "Long") {
      return CILOSTAZOLFrameSlotKind.Long;
    }
    if (type.getNamespace() == "System" && type.getName() == "Double") {
      return CILOSTAZOLFrameSlotKind.Double;
    }
    if (type.getNamespace() == "System" && type.getName() == "Float") {
      return CILOSTAZOLFrameSlotKind.Float;
    } else {
      return CILOSTAZOLFrameSlotKind.Object;
    }
  }

  // region StackManipulation
  public static void putObject(Frame frame, int slot, Object value) {
    assert slot >= 0;
    assert value != null;
    frame.setObjectStatic(slot, value);
  }

  public static void putInt(Frame frame, int slot, int value) {
    assert slot >= 0;
    frame.setIntStatic(slot, value);
  }

  public static void putFloat(Frame frame, int slot, float value) {
    assert slot >= 0;
    frame.setFloatStatic(slot, value);
  }

  public static void putLong(Frame frame, int slot, long value) {
    assert slot >= 0;
    frame.setLongStatic(slot, value);
  }

  public static void putDouble(Frame frame, int slot, double value) {
    assert slot >= 0;
    frame.setDoubleStatic(slot, value);
  }

  public static void putByte(Frame frame, int slot, byte value) {
    assert slot >= 0;
    frame.setByteStatic(slot, value);
  }

  public static void putBoolean(Frame frame, int slot, boolean value) {
    assert slot >= 0;
    frame.setBooleanStatic(slot, value);
  }

  public enum CILOSTAZOLFrameSlotKind {
    Object,
    Boolean,
    Byte,
    Int,
    Long,
    Double,
    Float
  }
  // endregion
}
