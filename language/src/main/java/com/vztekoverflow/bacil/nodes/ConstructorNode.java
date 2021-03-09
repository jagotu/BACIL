package com.vztekoverflow.bacil.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.vztekoverflow.bacil.runtime.BACILMethod;
import com.vztekoverflow.bacil.runtime.StaticObject;
import com.vztekoverflow.bacil.runtime.types.NamedType;

public class ConstructorNode extends CallableNode {

    protected final BACILMethod method;

    protected final int top;
    protected final NamedType objType;

    @Child private DirectCallNode directCallNode;


    public ConstructorNode(BACILMethod method, int top) {
        this.method = method;
        this.top = top;
        this.objType = (NamedType)method.getDefiningType();
        directCallNode = DirectCallNode.create(this.method.getMethodCallTarget());
    }

    @Override
    public int execute(VirtualFrame frame, long[] primitives, Object[] refs) {
        Object[] args = BytecodeNode.prepareArgs(primitives, refs, top, method, 1);
        StaticObject obj = new StaticObject(objType);
        args[0] = obj;

        directCallNode.call(args);
        final int firstArg = top - method.getArgsCount() + 1;
        refs[firstArg] = obj;
        return firstArg+1;
    }

}
