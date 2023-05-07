package com.vztekoverflow.cilostazol.nodes;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.IMethod;

public class CILOSTAZOLRootNode extends RootNode {
    protected final CILMethodNode _node;
    private CILOSTAZOLRootNode(FrameDescriptor descriptor, CILMethodNode node) {
        super(node.getMethod().getDefiningComponent().getDefiningAssembly().getAppDomain().getContext().getLanguage(), descriptor);
        _node = node;
    }

    public static CILOSTAZOLRootNode create(IMethod method, byte[] cilCode) {
        final CILMethodNode node = CILMethodNode.create(method, cilCode);
        return new CILOSTAZOLRootNode(node.getFrameDescriptor(), node);
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return _node.execute(frame);
    }
}
