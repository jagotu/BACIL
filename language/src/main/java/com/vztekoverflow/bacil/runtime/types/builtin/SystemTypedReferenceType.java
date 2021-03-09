package com.vztekoverflow.bacil.runtime.types.builtin;

import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITypeDefTableRow;

public class SystemTypedReferenceType extends SystemValueTypeType {
    public SystemTypedReferenceType(CLITypeDefTableRow type, CLIComponent component) {
        super(type, component);
    }
}
