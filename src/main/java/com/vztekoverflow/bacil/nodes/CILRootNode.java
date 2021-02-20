package com.vztekoverflow.bacil.nodes;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

public class CILRootNode extends RootNode {

    @Child protected BytecodeNode methodNode;

    public CILRootNode(FrameDescriptor frameDescriptor, BytecodeNode methodNode) {
        super(methodNode.getMethod().getComponent().getLanguage(), frameDescriptor);
        this.methodNode = methodNode;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return methodNode.execute(frame);
    }

    @Override
    public String toString() {
        return methodNode.getMethod().toString();
    }
}
