package com.vztekoverflow.bacil.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.TypedField;

public class LdsfldaNode extends CallableNode {

    private final TypedField field;
    private final Type objType;
    private final int top;

    public LdsfldaNode(CLITablePtr token, CLIComponent callingComponent, int top, Type objType) {
        this.top = top;
        this.objType = objType;
        field = objType.getTypedField(token, callingComponent);

        if(!field.isStatic())
        {
            throw new BACILInternalError("LDSFLDA for an instance field!");
        }
    }

    @Override
    public int execute(VirtualFrame frame, long[] primitives, Object[] refs) {
        refs[top] = objType.getStaticFieldReference(field);
        return top+1;
    }
}
