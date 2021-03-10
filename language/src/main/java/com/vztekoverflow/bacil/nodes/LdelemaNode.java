package com.vztekoverflow.bacil.nodes;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.runtime.ExecutionStackPrimitiveMarker;
import com.vztekoverflow.bacil.runtime.LocationReference;
import com.vztekoverflow.bacil.runtime.SZArray;
import com.vztekoverflow.bacil.runtime.types.Type;

public class LdelemaNode extends CallableNode {

    private final Type elementType;
    private final int top;

    public LdelemaNode(Type elementType, int top) {
        this.elementType = elementType;
        this.top = top;
    }

    @Override
    public int execute(VirtualFrame frame, long[] primitives, Object[] refs) {

        if(refs[top-1] != ExecutionStackPrimitiveMarker.EXECUTION_STACK_INT32)
        {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("Only INT32 supported as SZArray index");
        }

        int index = (int)primitives[top-1];

        SZArray array = (SZArray) refs[top-2];
        refs[top-2] = new LocationReference(array.getFieldsHolder(), index);

        return top-1;
    }
}
