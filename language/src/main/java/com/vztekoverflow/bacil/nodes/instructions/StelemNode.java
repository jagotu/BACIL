package com.vztekoverflow.bacil.nodes.instructions;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.vztekoverflow.bacil.nodes.BytecodeNode;
import com.vztekoverflow.bacil.nodes.EvaluationStackAwareNode;
import com.vztekoverflow.bacil.runtime.types.Type;


/**
 * A Truffle node representing the stelem instruction.
 * (III.4.26 stelem – store element to array)
 * Stores the resolved element type from the instruction.
 */
public class StelemNode extends EvaluationStackAwareNode {

    private final Type elementType;
    private final int top;

    /**
     * Create a new node representing the stelem instruction.
     *
     * @param elementType resolved element type from the instruction
     * @param top stack top when running this instruction
     */
    public StelemNode(Type elementType, int top) {
        this.elementType = elementType;
        this.top = top;
    }

    @Override
    public int execute(VirtualFrame frame, long[] primitives, Object[] refs) {
        //We just call the BytecodeNode implementation used for the shorthand
        //stelem.<type> (III.4.27) instructions with the stored type.
        BytecodeNode.storeArrayElem(elementType, primitives, refs, top);
        return top-3;
    }
}
