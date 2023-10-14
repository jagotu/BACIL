package com.vztekoverflow.bacil.runtime.types.builtin;

import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITypeDefTableRow;

/**
 * Implementation of the System.Void type.
 */
public class SystemVoidType extends SystemValueTypeType {
    public SystemVoidType(CLITypeDefTableRow type, CLIComponent component) {
        super(type, component);
    }

    //Nobody should ever work with System.Void (other than return type), so no implementation necessary.
}
