package com.vztekoverflow.bacil.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.TypedField;

public class LdsfldNode extends CallableNode {

    private final TypedField field;
    private final Type objType;
    private final int top;

    public LdsfldNode(CLITablePtr token, CLIComponent callingComponent, int top, Type objType) {
        this.top = top;
        this.objType = objType;
        field = objType.getTypedField(token, callingComponent);

        if(!field.isStatic())
        {
            throw new BACILInternalError("LDSFLD for an instance field!");
        }
    }

    @Override
    public int execute(VirtualFrame frame, long[] primitives, Object[] refs) {
        objType.staticFieldToStackVar(field, refs, primitives, top);
        return top+1;
    }
}
