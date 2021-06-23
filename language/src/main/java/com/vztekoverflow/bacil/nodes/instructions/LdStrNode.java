package com.vztekoverflow.bacil.nodes.instructions;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.vztekoverflow.bacil.nodes.EvaluationStackAwareNode;

/**
 * A Truffle node representing the ldstr instruction.
 * (III.4.16 ldstr – load a literal string)
 * Stores the resolved string from the instruction.
 */
public class LdStrNode extends EvaluationStackAwareNode {

    protected final String value;
    private final int top;

    /**
     * Create a new node representing the ldstr instruction.
     * @param value the string literal resolved from the instruction
     * @param top stack top when running this instruction
     */
    public LdStrNode(String value, int top) {
        this.value = value;
        this.top = top;
    }

    @Override
    public int execute(VirtualFrame frame, long[] primitives, Object[] refs) {
        refs[top] = value;
        return top + 1;
    }
}
