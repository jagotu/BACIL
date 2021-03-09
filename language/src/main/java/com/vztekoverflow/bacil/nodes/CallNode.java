package com.vztekoverflow.bacil.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.vztekoverflow.bacil.runtime.BACILMethod;
import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.builtin.SystemVoidType;

public class CallNode extends CallableNode {

    protected final BACILMethod method;

    protected final int top;

    @Child private DirectCallNode directCallNode;


    public CallNode(BACILMethod method, int top) {
        this.method = method;
        this.top = top;
        directCallNode = DirectCallNode.create(this.method.getMethodCallTarget());
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
