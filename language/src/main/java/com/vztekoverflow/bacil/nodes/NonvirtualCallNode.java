package com.vztekoverflow.bacil.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.vztekoverflow.bacil.runtime.BACILMethod;
import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.builtin.SystemVoidType;

public class NonvirtualCallNode extends CallableNode {

    protected final BACILMethod method;

    protected final int top;

    @Child private com.oracle.truffle.api.nodes.DirectCallNode directCallNode;


    public NonvirtualCallNode(BACILMethod method, int top) {
        this.method = method;
        this.top = top;
        directCallNode = com.oracle.truffle.api.nodes.DirectCallNode.create(this.method.getMethodCallTarget());
    }

    @Override
    public int execute(VirtualFrame frame, long[] primitives, Object[] refs) {
        Object[] args = BytecodeNode.prepareArgs(primitives, refs, top, method, 0);
        Object returnValue = directCallNode.call(args);
        Type returnType = method.getRetType();
        final int firstArg = top - method.getArgsCount();

        if(!(returnType instanceof SystemVoidType))
        {
            returnType.objectToStack(refs, primitives, firstArg, returnValue);
            return firstArg+1;
        }
        return firstArg;
    }

}
