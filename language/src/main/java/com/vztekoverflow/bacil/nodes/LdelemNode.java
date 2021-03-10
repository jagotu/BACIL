package com.vztekoverflow.bacil.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.vztekoverflow.bacil.runtime.types.Type;

public class LdelemNode extends CallableNode {

    private final Type elementType;
    private final int top;

    public LdelemNode(Type elementType, int top) {
        this.elementType = elementType;
        this.top = top;
    }

    @Override
    public int execute(VirtualFrame frame, long[] primitives, Object[] refs) {

        BytecodeNode.loadArrayElem(elementType, primitives, refs, top);
        return top-1;
    }
}
