package com.vztekoverflow.bacil.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;

public class LdStrNode extends CallableNode {

    protected final String value;
    private final int top;

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
