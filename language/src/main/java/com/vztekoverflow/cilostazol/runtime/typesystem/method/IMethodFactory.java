package com.vztekoverflow.cilostazol.runtime.typesystem.method;

import com.vztekoverflow.cil.parser.cli.table.generated.CLIMethodDefTableRow;

public interface IMethodFactory{
        public IMethod create(CLIMethodDefTableRow type);
}
