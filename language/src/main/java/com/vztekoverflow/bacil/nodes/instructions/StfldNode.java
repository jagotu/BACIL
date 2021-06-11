package com.vztekoverflow.bacil.nodes.instructions;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.nodes.EvaluationStackAwareNode;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.runtime.StaticObject;
import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.TypedField;

/**
 * A Truffle node representing the stfld instruction.
 * (III.4.28 stfld – store into a field of an object)
 * Stores the resolved object type and the field from the instruction.
 */
public class StfldNode extends EvaluationStackAwareNode {

    private final TypedField field;
    private final Type objType;
    private final int top;

    /**
     * Create a new node representing the stfld instruction.
     * @param token token representing the field, pointing to either member_ref or field tables
     * @param callingComponent the {@link CLIComponent} where the instruction is
     * @param top stack top when running this instruction
     * @param objType resolved type where the field is defined
     */
    public StfldNode(CLITablePtr token, CLIComponent callingComponent, int top, Type objType) {
        this.top = top;
        this.objType = objType;
        field = objType.getTypedField(token, callingComponent);
        if(field.isStatic())
        {
            throw new BACILInternalError("STFLD for a static field!");
        }
    }

    @Override
    public int execute(VirtualFrame frame, long[] primitives, Object[] refs) {
        StaticObject obj = (StaticObject)refs[top-2];
        objType.instanceFieldFromStackVar(obj, field, refs[top-1], primitives[top-1]);
        return top-2;
    }
}
