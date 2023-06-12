package com.vztekoverflow.bacil.runtime.types.builtin;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITypeDefTableRow;
import com.vztekoverflow.bacil.runtime.EvaluationStackPrimitiveMarker;
import com.vztekoverflow.bacil.runtime.locations.LocationsHolder;
import com.vztekoverflow.bacil.runtime.types.TypeHelpers;

/** Implementation of the System.UInt16 type. */
public class SystemUInt16Type extends SystemValueTypeType {

  public SystemUInt16Type(CLITypeDefTableRow type, CLIComponent component) {
    super(type, component);
  }

  @Override
  public void locationToStack(
      LocationsHolder holder,
      int primitiveOffset,
      int refOffset,
      Object[] refs,
      long[] primitives,
      int slot) {
    refs[slot] = EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32;
    primitives[slot] = TypeHelpers.zeroExtend16(holder.getPrimitives()[primitiveOffset]);
  }

  @Override
  public void stackToLocation(
      LocationsHolder holder, int primitiveOffset, int refOffset, Object ref, long primitive) {
    if (ref != EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32) {
      CompilerDirectives.transferToInterpreterAndInvalidate();
      throw new BACILInternalError("Saving a non-Int32 value into System.UInt16 location.");
    }
    holder.getPrimitives()[primitiveOffset] = TypeHelpers.truncate16(primitive);
  }

  @Override
  public void objectToStack(Object[] refs, long[] primitives, int slot, Object value) {
    refs[slot] = EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32;
    primitives[slot] = TypeHelpers.zeroExtend16((Short) value);
  }

  @Override
  public Object stackToObject(Object ref, long primitive) {
    if (ref != EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32) {
      CompilerDirectives.transferToInterpreterAndInvalidate();
      throw new BACILInternalError("Accessing a non-Int32 value from a System.UInt16 location.");
    }
    return (short) primitive;
  }

  @Override
  public void objectToLocation(
      LocationsHolder holder, int primitiveOffset, int refOffset, Object value) {
    holder.getPrimitives()[primitiveOffset] = TypeHelpers.zeroExtend16((Short) value);
  }

  @Override
  public Object locationToObject(LocationsHolder holder, int primitiveOffset, int refOffset) {
    return (short) holder.getPrimitives()[primitiveOffset];
  }

  @Override
  public Object initialValue() {
    return (short) 0;
  }

  @Override
  public int getStorageType() {
    return STORAGE_PRIMITIVE;
  }
}
