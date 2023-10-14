package com.vztekoverflow.bacil.nodes.instructions;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.nodes.EvaluationStackAwareNode;
import com.vztekoverflow.bacil.runtime.EvaluationStackPrimitiveMarker;
import com.vztekoverflow.bacil.runtime.LocationReference;
import com.vztekoverflow.bacil.runtime.SZArray;
import com.vztekoverflow.bacil.runtime.types.CLIType;
import com.vztekoverflow.bacil.runtime.types.Type;

/**
 * A Truffle node representing the ldelema instruction.
 * (III.4.9 ldelema – load address of an element of an array)
 * Stores the resolved elementType from the instruction.
 */
public class LdelemaNode extends EvaluationStackAwareNode {

    private final Type elementType;
    private final int top;

    private final int primitiveSize;
    private final int refSize;


    /**
     * Create a new node representing the ldelema instruction.
     * @param elementType type of the array element
     * @param top tack top when running this instruction
     */
    public LdelemaNode(Type elementType, int top) {
        this.elementType = elementType;
        this.top = top;

        if(elementType.getStorageType() == Type.STORAGE_VALUETYPE)
        {
            CLIType cliType = (CLIType) elementType;
            primitiveSize = cliType.getInstanceFieldsDescriptor().getPrimitiveCount();
            refSize = cliType.getInstanceFieldsDescriptor().getRefCount();
        } else {
            primitiveSize = 1;
            refSize = 1;
        }
    }

    @Override
    public int execute(VirtualFrame frame, long[] primitives, Object[] refs) {

        //Breaks standard: We should also support native int here, but for us
        //native int is 64-bit, and Java arrays only use 32-bit indexers.
        if(refs[top-1] != EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32)
        {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new BACILInternalError("Only INT32 supported as SZArray index");
        }


        int index = (int)primitives[top-1];
        SZArray array = (SZArray) refs[top-2]; //ldelema only works on SZArrays
        refs[top-2] = new LocationReference(array.getFieldsHolder(), primitiveSize*index, refSize*index, elementType);

        return top-1;
    }
}
