package com.vztekoverflow.bacil.runtime.types.builtin;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITypeDefTableRow;
import com.vztekoverflow.bacil.runtime.EvaluationStackPrimitiveMarker;
import com.vztekoverflow.bacil.runtime.types.locations.LocationsHolder;

public class SystemUInt64Type extends SystemValueTypeType {
    public SystemUInt64Type(CLITypeDefTableRow type, CLIComponent component) {
        super(type, component);
    }


    @Override
    public void locationToStack(LocationsHolder holder, int holderOffset, Object[] refs, long[] primitives, int slot) {
        refs[slot] = EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT64;
        primitives[slot] = holder.getPrimitives()[holderOffset];
    }

    @Override
    public void stackToLocation(LocationsHolder holder, int holderOffset, Object ref, long primitive) {
        if(ref != EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT64)
        {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("Saving a non-Int64 value into System.UInt64 location.");
        }
        holder.getPrimitives()[holderOffset]= primitive;
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
    public void objectToLocation(LocationsHolder holder, int holderOffset, Object obj) {
        holder.getPrimitives()[holderOffset] = (Long) obj;
    }

    @Override
    public Object locationToObject(LocationsHolder holder, int holderOffset) {
        return holder.getPrimitives()[holderOffset];
    }

    @Override
    public Object initialValue() {
        return 0L;
    }
}
