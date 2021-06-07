package com.vztekoverflow.bacil.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.vztekoverflow.bacil.runtime.types.Type;

/**
 * A Truffle node representing the box instruction.
 * (III.4.1 box – convert a boxable value to its boxed form)
 * Stores the resolved type from the instruction.
 */
public class BoxNode extends EvaluationStackAwareNode {

    private final Type type;
    private final int top;

    /**
     * Create a new node representing the Box instruction.
     *
     * @param type resolved type from the instruction
     * @param top stack top when running this instruction
     */
    public BoxNode(Type type, int top) {
        this.top = top;
        this.type = type;
    }

    @Override
    public int execute(VirtualFrame frame, long[] primitives, Object[] refs) {
        refs[top-1] = type.stackToObject(refs[top-1], primitives[top-1]);
        return top;
    }
}
