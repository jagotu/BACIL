package com.vztekoverflow.bacil.runtime.types.builtin;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITypeDefTableRow;
import com.vztekoverflow.bacil.runtime.EvaluationStackPrimitiveMarker;
import com.vztekoverflow.bacil.runtime.locations.LocationsHolder;

/**
 * Implementation of the System.UInt64 type.
 */
public class SystemUInt64Type extends SystemValueTypeType {
    public SystemUInt64Type(CLITypeDefTableRow type, CLIComponent component) {
        super(type, component);
    }


    @Override
    public void locationToStack(LocationsHolder holder, int primitiveOffset, int refOffset, Object[] refs, long[] primitives, int slot) {
        refs[slot] = EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT64;
        primitives[slot] = holder.getPrimitives()[primitiveOffset];
    }

    @Override
    public void stackToLocation(LocationsHolder holder, int primitiveOffset, int refOffset, Object ref, long primitive) {
        if(ref != EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT64)
        {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("Saving a non-Int64 value into System.UInt64 location.");
        }
        holder.getPrimitives()[primitiveOffset]= primitive;
    }

    @Override
    public void objectToStack(Object[] refs, long[] primitives, int slot, Object value) {
        refs[slot] = EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT64;
        primitives[slot] = (Long)value;
    }

    @Override
    public Object stackToObject(Object ref, long primitive) {
        if(ref != EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT64)
        {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("Accessing a non-Int64 value from a System.UInt64 location.");
        }
        return primitive;
    }

    @Override
    public void objectToLocation(LocationsHolder holder, int primitiveOffset, int refOffset, Object value) {
        holder.getPrimitives()[primitiveOffset] = (Long) value;
    }

    @Override
    public Object locationToObject(LocationsHolder holder, int primitiveOffset, int refOffset) {
        return holder.getPrimitives()[primitiveOffset];
    }

    @Override
    public Object initialValue() {
        return 0L;
    }


    @Override
    public int getStorageType() {
        return STORAGE_PRIMITIVE;
    }
}
