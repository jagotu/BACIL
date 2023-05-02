package com.vztekoverflow.cilostazol.runtime.typesystem.method;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cil.parser.cli.table.generated.CLIMethodDefTableRow;
import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;

public class MethodFactory implements IMethodFactory {
    private final CLIFile _file;

    //region IMethodFactory
    @Override
    public IMethod create(CLIMethodDefTableRow type) {
        throw new NotImplementedException();
    }
    //endregion

    public MethodFactory(CLIFile file) {
        _file = file;
    }
}
