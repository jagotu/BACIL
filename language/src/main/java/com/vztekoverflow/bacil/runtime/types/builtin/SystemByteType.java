package com.vztekoverflow.bacil.runtime.types.builtin;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITypeDefTableRow;
import com.vztekoverflow.bacil.runtime.ExecutionStackPrimitiveMarker;
import com.vztekoverflow.bacil.runtime.types.TypeHelpers;
import com.vztekoverflow.bacil.runtime.types.locations.LocationsHolder;

public class SystemByteType extends SystemValueTypeType {
    public SystemByteType(CLITypeDefTableRow type, CLIComponent component) {
        super(type, component);
    }

    @Override
    public void locationToStack(LocationsHolder holder, int holderOffset, Object[] refs, long[] primitives, int slot) {
        refs[slot] = ExecutionStackPrimitiveMarker.EXECUTION_STACK_INT32;
        primitives[slot] = TypeHelpers.zeroExtend8(holder.getPrimitives()[holderOffset]);
    }

    @Override
    public void stackToLocation(LocationsHolder holder, int holderOffset, Object ref, long primitive) {
        if(ref != ExecutionStackPrimitiveMarker.EXECUTION_STACK_INT32)
        {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("Saving a non-Int32 value into System.Byte location.");
        }
        holder.getPrimitives()[holderOffset]= TypeHelpers.truncate8(primitive);
    }

    @Override
    public void objectToStack(Object[] refs, long[] primitives, int slot, Object value) {
        refs[slot] = ExecutionStackPrimitiveMarker.EXECUTION_STACK_INT32;
        primitives[slot] = TypeHelpers.zeroExtend8((Short)value);
    }

    @Override
    public Object stackToObject(Object ref, long primitive) {
        if(ref != ExecutionStackPrimitiveMarker.EXECUTION_STACK_INT32)
        {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("Accessing a non-Int32 value from a System.Byte location.");
        }
        return (byte) primitive;
    }

    @Override
    public void objectToLocation(LocationsHolder holder, int holderOffset, Object obj) {
        holder.getPrimitives()[holderOffset] = TypeHelpers.zeroExtend8((Short) obj);
    }

    @Override
    public Object locationToObject(LocationsHolder holder, int holderOffset) {
        return (byte)holder.getPrimitives()[holderOffset];
    }

    @Override
    public Object initialValue() {
        return (byte)0;
    }
}
