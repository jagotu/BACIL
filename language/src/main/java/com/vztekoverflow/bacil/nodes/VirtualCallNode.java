package com.vztekoverflow.bacil.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.runtime.BACILMethod;
import com.vztekoverflow.bacil.runtime.StaticObject;
import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.builtin.SystemVoidType;
import com.vztekoverflow.bacil.runtime.types.locations.VtableSlotIdentity;

public class VirtualCallNode extends CallableNode {

    protected final int slot;
    protected final int top;

    public VirtualCallNode(BACILMethod method, int top) {
        Type t = method.getDefiningType();
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
    }

    @Override
    public int execute(VirtualFrame frame, long[] primitives, Object[] refs) {
        StaticObject obj = (StaticObject)refs[top-1];
        BACILMethod method = obj.getType().getVtable()[slot];

        Object[] args = BytecodeNode.prepareArgs(primitives, refs, top, method, 0);
        Object returnValue = method.getMethodCallTarget().call(args);
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
