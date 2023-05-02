package com.vztekoverflow.cilostazol.runtime.typesystem.type;

import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeDefTableRow;

public interface ITypeFactory {
    public IType create(CLITypeDefTableRow type);
}
