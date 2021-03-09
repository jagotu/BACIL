package com.vztekoverflow.bacil.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

public abstract class CallableNode extends Node {

    public abstract int execute(VirtualFrame frame, long[] primitives, Object[] refs);
}
