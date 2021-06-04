package com.vztekoverflow.bacil.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

/**
 * Represents a Truffle node that is aware of the BACIL execution stack,
 * taking {@code long[] primitives} and {@code Object[] refs} as explicit arguments.
 */
public abstract class ExecutionStackAwareNode extends Node {

    /**
     * Execute the node on the given execution stack.
     * @param frame the frame of the currently executing guest language method
     * @param primitives array representing the primitives on the stack
     * @param refs array representing references and {@link com.vztekoverflow.bacil.runtime.ExecutionStackPrimitiveMarker}s on the stack
     * @return the new execution stack top
     */
    public abstract int execute(VirtualFrame frame, long[] primitives, Object[] refs);
}
