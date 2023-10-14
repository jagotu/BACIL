package com.vztekoverflow.bacil.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.runtime.BACILMethod;
import com.vztekoverflow.bacil.runtime.StaticObject;
import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.VtableSlotIdentity;
import com.vztekoverflow.bacil.runtime.types.builtin.SystemVoidType;

/**
 * A Truffle node representing the callvirt instruction <b>only when the target method is actually virtual</b>.
 * (III.4.2 callvirt – call a method associated, at runtime, with an object)
 * Stores the resolved vtable slot and the root method from the instruction.
 */
public class VirtualCallNode extends EvaluationStackAwareNode {

    protected final int slot;
    protected final int top;
    protected final BACILMethod rootMethod;

    /**
     * Create a new node representing the callvirt instruction to a virtual method.
     * @param method the root method to call
     * @param top  stack top when running this instruction
     */
    public VirtualCallNode(BACILMethod method, int top) {

        //Find a vtable slot in the defining type resolving the specified root method
        VtableSlotIdentity[] identities = method.getDefiningType().getVtableSlots();
        int foundSlot = -1;
        for(int i = 0; i < identities.length; i++) {
            if (identities[i].resolves(method))
            {
                foundSlot = i;
                break;
            }
        }

        if(foundSlot == -1)
        {
            throw new BACILInternalError("Failed to resolve virtual function " + method.toString());
        }

        this.slot = foundSlot;
        this.top = top;
        this.rootMethod = method;
    }

    @Override
    public int execute(VirtualFrame frame, long[] primitives, Object[] refs) {
        //Get the "this" object
        StaticObject obj = (StaticObject)refs[top-1];
        //Resolve the actual method using the vtable
        BACILMethod method = obj.getType().getVtable()[slot];

        //Call the method
        Object[] args = BytecodeNode.prepareArgs(primitives, refs, top, rootMethod, 0);
        Object returnValue = method.getMethodCallTarget().call(args);

        //Put returned value on the evaluation stack
        Type returnType = rootMethod.getRetType();
        final int firstArg = top - rootMethod.getArgsCount();

        if(!(returnType instanceof SystemVoidType))
        {
            returnType.objectToStack(refs, primitives, firstArg, returnValue);
            return firstArg+1;
        }

        return firstArg;
    }

}
