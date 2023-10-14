package com.vztekoverflow.bacil.nodes;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

/**
 * The root node for a method with a {@link BytecodeNode} body.
 */
public class CILRootNode extends RootNode {

    @Child protected BytecodeNode methodNode;

    /**
     * Create a new root node for a method with a {@link BytecodeNode} body.
     * @param frameDescriptor descriptor of the slots of frame objects
     * @param methodNode method body as a {@link BytecodeNode}
     */
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
