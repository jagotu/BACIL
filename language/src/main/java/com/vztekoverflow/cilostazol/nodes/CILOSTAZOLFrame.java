package com.vztekoverflow.cilostazol.nodes;

import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.vztekoverflow.cilostazol.meta.SystemTypes;
import com.vztekoverflow.cilostazol.runtime.objectmodel.StaticObject;
import com.vztekoverflow.cilostazol.runtime.symbols.MethodSymbol;

import java.util.Objects;

public final class CILOSTAZOLFrame {
  public static FrameDescriptor create(int locals, int stack) {
    int slotCount = locals + stack;
    FrameDescriptor.Builder builder = FrameDescriptor.newBuilder(slotCount);
    builder.addSlots(slotCount, FrameSlotKind.Static);
    return builder.build();
  }

  // region stack offsets
  public static int getStartStackOffset(MethodSymbol method) {
    return getStartArgsOffset(method) + method.getParameters().length;
  }

  public static int getStartArgsOffset(MethodSymbol methodSymbol) {
    return getStartLocalsOffset(methodSymbol) + methodSymbol.getLocals().length;
  }

  public static int getStartLocalsOffset(MethodSymbol methodSymbol) {
    return methodSymbol.getMethodFlags().hasFlag(MethodSymbol.MethodFlags.Flag.STATIC) ? 0 : 1;
  }
  // endregion

  // region stack put
  public static void putObject(Frame frame, int slot, StaticObject value) {
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
  // endregion

  // region stack pop
  public static int popInt(Frame frame, int slot) {
    assert slot >= 0;
    int result = frame.getIntStatic(slot);
    // Avoid keeping track of popped slots in FrameStates.
    clearPrimitive(frame, slot);
    return result;
  }

  public static float popFloat(Frame frame, int slot) {
    assert slot >= 0;
    float result = frame.getFloatStatic(slot);
    // Avoid keeping track of popped slots in FrameStates.
    clearPrimitive(frame, slot);
    return result;
  }

  public static long popLong(Frame frame, int slot) {
    assert slot >= 0;
    long result = frame.getLongStatic(slot);
    // Avoid keeping track of popped slots in FrameStates.
    clearPrimitive(frame, slot);
    return result;
  }

  public static double popDouble(Frame frame, int slot) {
    assert slot >= 0;
    double result = frame.getDoubleStatic(slot);
    // Avoid keeping track of popped slots in FrameStates.
    clearPrimitive(frame, slot);
    return result;
  }

  public static StaticObject popObject(Frame frame, int slot) {
    assert slot >= 0;
    Object result = frame.getObjectStatic(slot);
    // Avoid keeping track of popped slots in FrameStates.
    clearPrimitive(frame, slot);
    return (StaticObject) result;
  }

  private static void clearPrimitive(Frame frame, int slot) {
    assert slot >= 0;
    frame.clearPrimitiveStatic(slot);
  }
  // endregion

  // region stack set
  public static void setLocalObject(Frame frame, int localSlot, StaticObject value) {
    assert localSlot >= 0;
    assert value != null;
    frame.setObjectStatic(localSlot, value);
  }

  public static void setLocalInt(Frame frame, int localSlot, int value) {
    assert localSlot >= 0;
    frame.setIntStatic(localSlot, value);
  }

  public static void setLocalFloat(Frame frame, int localSlot, float value) {
    assert localSlot >= 0;
    frame.setFloatStatic(localSlot, value);
  }

  public static void setLocalLong(Frame frame, int localSlot, long value) {
    assert localSlot >= 0;
    frame.setLongStatic(localSlot, value);
  }

  public static void setLocalDouble(Frame frame, int localSlot, double value) {
    assert localSlot >= 0;
    frame.setDoubleStatic(localSlot, value);
  }
  // endregion

  // region stack get
  public static int getLocalInt(Frame frame, int localSlot) {
    assert localSlot >= 0;
    return frame.getIntStatic(localSlot);
  }

  public static StaticObject getLocalObject(Frame frame, int localSlot) {
    assert localSlot >= 0;
    Object result = frame.getObjectStatic(localSlot);
    assert result != null;
    return (StaticObject) result;
  }

  public static float getLocalFloat(Frame frame, int localSlot) {
    assert localSlot >= 0;
    return frame.getFloatStatic(localSlot);
  }

  public static long getLocalLong(Frame frame, int localSlot) {
    assert localSlot >= 0;
    return frame.getLongStatic(localSlot);
  }

  public static double getLocalDouble(Frame frame, int localSlot) {
    assert localSlot >= 0;
    return frame.getDoubleStatic(localSlot);
  }
  // endregion

  // region stack types
  public enum StackType {
    Object,
    Int,
    Long,
    Float,
    Double,
    Void,
  }

  // TODO: It should rely on Assembly as well...
  public static StackType getStackTypeKind(String name, String namespace) {
    if (Objects.equals(namespace, "System")) {
      switch (name) {
        case "Boolean":
        case "Byte":
        case "SByte":
        case "Char":
        case "Int16":
        case "UInt16":
        case "Int32":
        case "UInt32":
          return StackType.Int;
        case "Double":
          return StackType.Double;
        case "Single":
          return StackType.Float;
        case "Int64":
        case "UInt64":
          return StackType.Long;
          // Decimal, UIntPtr, IntPtr ??
      }
    }

    return StackType.Object;
  }
  // endregion

  public static void copy(Frame frame, int sourceSlot, int destSlot) {
    assert sourceSlot >= 0 && destSlot >= 0;
    frame.copy(sourceSlot, destSlot);
  }
}
