package com.vztekoverflow.bacil.runtime.types.builtin;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITypeDefTableRow;
import com.vztekoverflow.bacil.runtime.EvaluationStackPrimitiveMarker;
import com.vztekoverflow.bacil.runtime.types.locations.LocationsHolder;

public class SystemDoubleType extends SystemValueTypeType {
    public SystemDoubleType(CLITypeDefTableRow type, CLIComponent component) {
        super(type, component);
    }

    @Override
    public Object initialValue() {
        return (double)0;
    }

    @Override
    public void stackToLocation(LocationsHolder holder, int holderOffset, Object ref, long primitive) {
        if(ref != EvaluationStackPrimitiveMarker.EVALUATION_STACK_F)
        {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("Saving a non-Float value into System.Double location.");
        }
        holder.getPrimitives()[holderOffset]= primitive;
    }

    @Override
    public void locationToStack(LocationsHolder holder, int holderOffset, Object[] refs, long[] primitives, int slot) {
        refs[slot] = EvaluationStackPrimitiveMarker.EVALUATION_STACK_F;
        primitives[slot] = holder.getPrimitives()[holderOffset];
    }

    @Override
    public Object stackToObject(Object ref, long primitive) {
        if(ref != EvaluationStackPrimitiveMarker.EVALUATION_STACK_F)
        {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("Accessing a non-Float value from a System.Double location.");
        }
        return Double.longBitsToDouble(primitive);
    }

    @Override
    public void objectToStack(Object[] refs, long[] primitives, int slot, Object value) {
        refs[slot] = EvaluationStackPrimitiveMarker.EVALUATION_STACK_F;
        primitives[slot] = Double.doubleToLongBits((Double)value);
    }

    @Override
    public Object locationToObject(LocationsHolder holder, int holderOffset) {
        return Double.longBitsToDouble(holder.getPrimitives()[holderOffset]);
    }

    @Override
    public void objectToLocation(LocationsHolder holder, int holderOffset, Object obj) {
        holder.getPrimitives()[holderOffset] = Double.doubleToLongBits((Double) obj);
    }
}
