package com.vztekoverflow.bacil.runtime.types.builtin;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITypeDefTableRow;
import com.vztekoverflow.bacil.runtime.EvaluationStackPrimitiveMarker;
import com.vztekoverflow.bacil.runtime.locations.LocationsHolder;

/** Implementation of the System.Double type. */
public class SystemDoubleType extends SystemValueTypeType {
  public SystemDoubleType(CLITypeDefTableRow type, CLIComponent component) {
    super(type, component);
  }

  @Override
  public Object initialValue() {
    return (double) 0;
  }

  @Override
  public void stackToLocation(
      LocationsHolder holder, int primitiveOffset, int refOffset, Object ref, long primitive) {
    if (ref != EvaluationStackPrimitiveMarker.EVALUATION_STACK_F) {
      CompilerDirectives.transferToInterpreterAndInvalidate();
      throw new BACILInternalError("Saving a non-Float value into System.Double location.");
    }
    holder.getPrimitives()[primitiveOffset] = primitive;
  }

  @Override
  public void locationToStack(
      LocationsHolder holder,
      int primitiveOffset,
      int refOffset,
      Object[] refs,
      long[] primitives,
      int slot) {
    refs[slot] = EvaluationStackPrimitiveMarker.EVALUATION_STACK_F;
    primitives[slot] = holder.getPrimitives()[primitiveOffset];
  }

  @Override
  public Object stackToObject(Object ref, long primitive) {
    if (ref != EvaluationStackPrimitiveMarker.EVALUATION_STACK_F) {
      CompilerDirectives.transferToInterpreterAndInvalidate();
      throw new BACILInternalError("Accessing a non-Float value from a System.Double location.");
    }
    return Double.longBitsToDouble(primitive);
  }

  @Override
  public void objectToStack(Object[] refs, long[] primitives, int slot, Object value) {
    refs[slot] = EvaluationStackPrimitiveMarker.EVALUATION_STACK_F;
    primitives[slot] = Double.doubleToLongBits((Double) value);
  }

  @Override
  public Object locationToObject(LocationsHolder holder, int primitiveOffset, int refOffset) {
    return Double.longBitsToDouble(holder.getPrimitives()[primitiveOffset]);
  }

  @Override
  public void objectToLocation(
      LocationsHolder holder, int primitiveOffset, int refOffset, Object value) {
    holder.getPrimitives()[primitiveOffset] = Double.doubleToLongBits((Double) value);
  }

  @Override
  public int getStorageType() {
    return STORAGE_PRIMITIVE;
  }
}
