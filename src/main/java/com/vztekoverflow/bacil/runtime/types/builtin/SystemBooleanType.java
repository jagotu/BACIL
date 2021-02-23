package com.vztekoverflow.bacil.runtime.types.builtin;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITypeDefTableRow;
import com.vztekoverflow.bacil.runtime.ExecutionStackPrimitiveMarker;
import com.vztekoverflow.bacil.runtime.types.locations.LocationsHolder;

public class SystemBooleanType extends SystemValueTypeType {
    public SystemBooleanType(CLITypeDefTableRow type, CLIComponent component) {
        super(type, component);
    }

    @Override
    public void locationToStack(LocationsHolder holder, int holderOffset, Object[] refs, long[] primitives, int slot) {
        refs[slot] = ExecutionStackPrimitiveMarker.EXECUTION_STACK_INT32;
        primitives[slot] = holder.getPrimitives()[holderOffset];
    }

    @Override
    public void stackToLocation(LocationsHolder holder, int holderOffset, Object ref, long primitive) {
        if(ref != ExecutionStackPrimitiveMarker.EXECUTION_STACK_INT32)
        {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("Saving a non-Int32 value into System.Boolean location.");
        }
        holder.getPrimitives()[holderOffset]= primitive;
    }

    @Override
    public void objectToStack(Object[] refs, long[] primitives, int slot, Object value) {
        refs[slot] = ExecutionStackPrimitiveMarker.EXECUTION_STACK_INT32;
        primitives[slot] = ((Boolean)value ? 1 : 0);
    }

    @Override
    public Object stackToObject(Object ref, long primitive) {
        if(ref != ExecutionStackPrimitiveMarker.EXECUTION_STACK_INT32)
        {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("Saving a non-Int32 value into System.Boolean location.");
        }
        return primitive != 0;
    }

    @Override
    public void objectToLocation(LocationsHolder holder, int holderOffset, Object obj) {
        holder.getPrimitives()[holderOffset] = ((Boolean) obj) ? 1 : 0;
    }

    @Override
    public Object locationToObject(LocationsHolder holder, int holderOffset) {
        return holder.getPrimitives()[holderOffset] != 1;
    }

    @Override
    public Object initialValue() {
        return 0;
    }

    @Override
    public int getPrimitiveStorageSize() {
        return 1;
    }
}
