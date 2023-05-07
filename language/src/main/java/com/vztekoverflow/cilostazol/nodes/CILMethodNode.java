package com.vztekoverflow.cilostazol.nodes;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.IMethod;

public class CILMethodNode extends CILNodeBase{
    private final IMethod _method;

    private CILMethodNode(IMethod method, byte[] cilCode) {
        _method = method;
    }

    public static CILMethodNode create(IMethod method, byte[] cilCode) {
        throw new NotImplementedException();
    }

    public IMethod getMethod() {
        return _method;
    }

    public FrameDescriptor getFrameDescriptor() {
        throw new NotImplementedException();
    }

    //region CILNodeBase
    @Override
    public Object execute(VirtualFrame frame) {
        throw new NotImplementedException();
    }
    //endregion
}
