package com.vztekoverflow.bacil.runtime.types.builtin;

import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITypeDefTableRow;
import com.vztekoverflow.bacil.runtime.types.NamedType;

public class SystemValueTypeType extends NamedType {
    public SystemValueTypeType(CLITypeDefTableRow type, CLIComponent component) {
        super(type, component);
    }
}
