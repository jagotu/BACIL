package com.vztekoverflow.bacil.runtime.types.builtin;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITypeDefTableRow;
import com.vztekoverflow.bacil.runtime.EvaluationStackPrimitiveMarker;
import com.vztekoverflow.bacil.runtime.locations.LocationsHolder;

/**
 * Implementation of the System.Boolean type.
 */
public class SystemBooleanType extends SystemValueTypeType {
    public SystemBooleanType(CLITypeDefTableRow type, CLIComponent component) {
        super(type, component);
    }

    @Override
    public void locationToStack(LocationsHolder holder, int primitiveOffset, int refOffset, Object[] refs, long[] primitives, int slot) {
        refs[slot] = EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32;
        primitives[slot] = holder.getPrimitives()[primitiveOffset];
    }

    @Override
    public void stackToLocation(LocationsHolder holder, int primitiveOffset, int refOffset, Object ref, long primitive) {
        if(ref != EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32)
        {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("Saving a non-Int32 value into System.Boolean location.");
        }
        holder.getPrimitives()[primitiveOffset]= primitive;
    }

    @Override
    public void objectToStack(Object[] refs, long[] primitives, int slot, Object value) {
        refs[slot] = EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32;
        primitives[slot] = ((Boolean)value ? 1 : 0);
    }

    @Override
    public Object stackToObject(Object ref, long primitive) {
        if(ref != EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32)
        {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("Saving a non-Int32 value into System.Boolean location.");
        }
        return primitive != 0;
    }

    @Override
    public void objectToLocation(LocationsHolder holder, int primitiveOffset, int refOffset, Object value) {
        holder.getPrimitives()[primitiveOffset] = ((Boolean) value) ? 1 : 0;
    }

    @Override
    public Object locationToObject(LocationsHolder holder, int primitiveOffset, int refOffset) {
        return holder.getPrimitives()[primitiveOffset] != 1;
    }

    @Override
    public Object initialValue() {
        return 0;
    }

    @Override
    public int getStorageType() {
        return STORAGE_PRIMITIVE;
    }
}
