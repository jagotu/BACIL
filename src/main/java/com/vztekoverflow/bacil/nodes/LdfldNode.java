package com.vztekoverflow.bacil.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.runtime.StaticObject;
import com.vztekoverflow.bacil.runtime.types.NamedType;
import com.vztekoverflow.bacil.runtime.types.TypedField;

public class LdfldNode extends CallableNode {

    private final TypedField field;
    private final int top;

    public LdfldNode(CLITablePtr token, CLIComponent callingComponent, int top, NamedType objType) {
        this.top = top;
        field = objType.getTypedField(token, callingComponent);
    }

    @Override
    public int execute(VirtualFrame frame, long[] primitives, Object[] refs) {
        StaticObject obj = (StaticObject)refs[top-1];
        obj.fieldToStackVar(field, refs, primitives, top-1);
        return top;
    }
}
