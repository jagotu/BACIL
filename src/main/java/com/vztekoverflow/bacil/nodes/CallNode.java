package com.vztekoverflow.bacil.nodes;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.Node;
import com.vztekoverflow.bacil.parser.cil.CILMethod;
import com.vztekoverflow.bacil.runtime.types.Type;

public class CallNode extends Node {

    @CompilerDirectives.CompilationFinal
    protected CILMethod method;

    protected final int top;

    @Child private DirectCallNode directCallNode;


    public CallNode(CILMethod method, int top) {
        this.method = method;
        this.top = top;
    }

    public int execute(VirtualFrame frame, long[] primitives, Object[] refs) {

        // TODO(peterssen): Constant fold this check.
        if (directCallNode == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            // insert call node though insertion method so that
            // stack frame iteration will see this node as parent
            directCallNode = insert(DirectCallNode.create(method.getCallTarget()));
        }

        Object[] args = BytecodeNode.prepareArgs(primitives, refs, top, method);
        Object returnValue = directCallNode.call(args);
        Type returnType = method.getMethodDefSig().getRetType();
        final int firstArg = top - method.getMethodDefSig().getParamCount();

        if(returnType.getTypeCategory() != Type.ELEMENT_TYPE_VOID)
        {
            returnType.toStackVar(refs, primitives, firstArg, returnValue);
            return firstArg+1;
        }
        return firstArg;
    }

}
