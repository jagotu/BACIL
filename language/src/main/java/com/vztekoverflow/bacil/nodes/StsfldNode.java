package com.vztekoverflow.bacil.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.TypedField;

/**
 * A Truffle node representing the stsfld instruction.
 * (III.4.30 stsfld – store a static field of a class)
 * Stores the resolved object type and the field from the instruction.
 */
public class StsfldNode extends EvaluationStackAwareNode {

    private final TypedField field;
    private final Type objType;
    private final int top;

    /**
     * Create a new node representing the stsfld instruction.
     * @param token token representing the field, pointing to either member_ref or field tables
     * @param callingComponent the {@link CLIComponent} where the instruction is
     * @param top stack top when running this instruction
     * @param objType resolved type where the field is defined
     */
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
