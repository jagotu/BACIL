package com.vztekoverflow.bacil.runtime.types.builtin;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITypeDefTableRow;
import com.vztekoverflow.bacil.runtime.ExecutionStackPrimitiveMarker;

public class SystemInt32Type extends SystemValueTypeType {
    public SystemInt32Type(CLITypeDefTableRow type, CLIComponent component) {
        super(type, component);
    }

    @Override
    public void toStackVar(Object[] refs, long[] primitives, int slot, Object value) {
        refs[slot] = ExecutionStackPrimitiveMarker.EXECUTION_STACK_INT32;
        primitives[slot] = (Integer)value;
    }

    @Override
    public Object fromStackVar(Object ref, long primitive) {
        if(ref != ExecutionStackPrimitiveMarker.EXECUTION_STACK_INT32)
        {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("Saving a non-Int32 value into System.Int32 location.");
        }
        return (int)primitive;
    }
}
