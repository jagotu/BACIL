package com.vztekoverflow.bacil.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.vztekoverflow.bacil.runtime.types.Type;

public class BoxNode extends CallableNode {

    private final Type type;
    private final int top;

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
