package com.vztekoverflow.bacil.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.vztekoverflow.bacil.runtime.types.Type;

/**
 * A Truffle node representing the ldelem instruction.
 * (III.4.7 ldelem – load element from array)
 * Stores the resolved element type from the instruction.
 */
public class LdelemNode extends ExecutionStackAwareNode {

    private final Type elementType;
    private final int top;

    /**
     * Create a new node representing the ldelem instruction.
     *
     * @param elementType resolved element type from the instruction
     * @param top stack top when running this instruction
     */
    public LdelemNode(Type elementType, int top) {
        this.elementType = elementType;
        this.top = top;
    }

    @Override
    public int execute(VirtualFrame frame, long[] primitives, Object[] refs) {
        //We just call the BytecodeNode implementation used for the shorthand
        //ldelem.<type> (III.4.8) instructions with the stored type.
        BytecodeNode.loadArrayElem(elementType, primitives, refs, top);
        return top-1;
    }
}
