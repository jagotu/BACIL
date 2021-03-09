package com.vztekoverflow.bacil.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.TypedField;

public class StsfldNode extends CallableNode {

    private final TypedField field;
    private final Type objType;
    private final int top;

    public StsfldNode(CLITablePtr token, CLIComponent callingComponent, int top, Type objType) {
        this.top = top;
        field = objType.getTypedField(token, callingComponent);
        this.objType = objType;
        if(!field.isStatic())
        {
            throw new BACILInternalError("STSFLD for an instance field!");
        }
    }

    @Override
    public int execute(VirtualFrame frame, long[] primitives, Object[] refs) {
        objType.staticFieldFromStackVar(field, refs[top-1], primitives[top-1]);
        return top-1;
    }
}
