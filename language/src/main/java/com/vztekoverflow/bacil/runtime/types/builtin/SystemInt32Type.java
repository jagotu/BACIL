package com.vztekoverflow.bacil.runtime.types.builtin;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITypeDefTableRow;
import com.vztekoverflow.bacil.runtime.EvaluationStackPrimitiveMarker;
import com.vztekoverflow.bacil.runtime.locations.LocationsHolder;
import com.vztekoverflow.bacil.runtime.types.TypeHelpers;

/**
 * Implementation of the System.Int32 type.
 */
public class SystemInt32Type extends SystemValueTypeType {
    public SystemInt32Type(CLITypeDefTableRow type, CLIComponent component) {
        super(type, component);
    }

    @Override
    public void locationToStack(LocationsHolder holder, int primitiveOffset, int refOffset, Object[] refs, long[] primitives, int slot) {
        refs[slot] = EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32;
        primitives[slot] = TypeHelpers.truncate32(holder.getPrimitives()[primitiveOffset]);
    }

    @Override
    public void stackToLocation(LocationsHolder holder, int primitiveOffset, int refOffset, Object ref, long primitive) {
        if(ref != EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32)
        {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("Saving a non-Int32 value into System.Int32 location.");
        }
        holder.getPrimitives()[primitiveOffset]= TypeHelpers.truncate32(primitive);
    }

    @Override
    public void objectToStack(Object[] refs, long[] primitives, int slot, Object value) {
        refs[slot] = EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32;
        primitives[slot] = TypeHelpers.truncate32((Integer)value);
    }

    @Override
    public Object stackToObject(Object ref, long primitive) {
        if(ref != EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32)
        {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("Accessing a non-Int32 value from a System.Int32 location.");
        }
        return (int) primitive;
    }

    @Override
    public void objectToLocation(LocationsHolder holder, int primitiveOffset, int refOffset, Object value) {
        holder.getPrimitives()[primitiveOffset] = TypeHelpers.truncate32((Integer) value);
    }

    @Override
    public Object locationToObject(LocationsHolder holder, int primitiveOffset, int refOffset) {
        return (int)holder.getPrimitives()[primitiveOffset];
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
