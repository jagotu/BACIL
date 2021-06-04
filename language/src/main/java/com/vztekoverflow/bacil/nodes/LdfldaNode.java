package com.vztekoverflow.bacil.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.runtime.StaticObject;
import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.TypedField;

/**
 * A Truffle node representing the ldflda instruction.
 * (III.4.11 ldflda – load field address)
 * Stores the resolved object type and the field from the instruction.
 */
public class LdfldaNode extends ExecutionStackAwareNode {

    private final TypedField field;
    private final int top;
    private final Type objType;

    /**
     * Create a new node representing the ldflda instruction.
     * @param token token representing the field, pointing to either member_ref or field tables
     * @param callingComponent the {@link CLIComponent} where the instruction is
     * @param top stack top when running this instruction
     * @param objType resolved type where the field is defined
     */
    public LdfldaNode(CLITablePtr token, CLIComponent callingComponent, int top, Type objType) {
        this.top = top;
        this.objType = objType;
        field = objType.getTypedField(token, callingComponent);
        if(field.isStatic())
        {
            throw new BACILInternalError("LDFLDA for a static field!");
        }
    }

    @Override
    public int execute(VirtualFrame frame, long[] primitives, Object[] refs) {
        StaticObject obj = (StaticObject)refs[top-1];
        refs[top-1] = objType.getInstanceFieldReference(field, obj);
        return top;
    }
}
