package com.vztekoverflow.bacil.runtime;

import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.runtime.types.NamedType;
import com.vztekoverflow.bacil.runtime.types.TypedField;

public class StaticObject {

    private final NamedType type;
    private final Object[] fields;

    public StaticObject(NamedType type) {
        type.initFields();
        this.type = type;
        this.fields = new Object[type.getFieldsCount()];
        for(int i = 0; i<type.getFieldsCount();i++)
        {
            fields[i] = type.getTypedField(i).getType().initialValue();
        }
    }

    public Object getField(CLITablePtr token, CLIComponent callingComponent)
    {
        return fields[type.getTypedField(token, callingComponent).getOffset()];
    }

    public void setField(CLITablePtr token, CLIComponent callingComponent, Object value)
    {
        fields[type.getTypedField(token, callingComponent).getOffset()] = value;
    }

    public void fieldToStackVar(CLITablePtr token, CLIComponent callingComponent, Object[] refs, long[] primitives, int slot)
    {
        TypedField field = type.getTypedField(token, callingComponent);
        field.getType().toStackVar(refs, primitives, slot, fields[field.getOffset()]);
    }

    public void fieldFromStackVar(CLITablePtr token, CLIComponent callingComponent, Object ref, long primitive)
    {
        TypedField field = type.getTypedField(token, callingComponent);
        fields[field.getOffset()] = field.getType().fromStackVar(ref, primitive);
    }


}
