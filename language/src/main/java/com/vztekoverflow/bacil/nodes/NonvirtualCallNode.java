package com.vztekoverflow.bacil.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.vztekoverflow.bacil.runtime.BACILMethod;
import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.builtin.SystemVoidType;

/**
 * A Truffle node representing the call instruction and callvirt <b>only when the target method is not actually virtual</b>.
 * (III.3.19 call – call a method)
 * (III.4.2 callvirt – call a method associated, at runtime, with an object)
 * Stores the resolved string from the instruction.
 */
public class NonvirtualCallNode extends EvaluationStackAwareNode {

    protected final BACILMethod method;
    protected final int top;
    @Child private com.oracle.truffle.api.nodes.DirectCallNode directCallNode;


    /**
     * Create a new node representing the call or non-virtual callvirt instructions.
     * @param method the method to call
     * @param top  stack top when running this instruction
     */
    public NonvirtualCallNode(BACILMethod method, int top) {
        this.method = method;
        this.top = top;
        directCallNode = com.oracle.truffle.api.nodes.DirectCallNode.create(this.method.getMethodCallTarget());
    }

    @Override
    public int execute(VirtualFrame frame, long[] primitives, Object[] refs) {
        //Execute method
        Object[] args = BytecodeNode.prepareArgs(primitives, refs, top, method, 0);
        Object returnValue = directCallNode.call(args);

        //Put returned value on the evaluation stack
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
