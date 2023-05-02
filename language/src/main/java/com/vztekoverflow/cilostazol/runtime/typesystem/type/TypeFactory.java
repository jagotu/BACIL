package com.vztekoverflow.cilostazol.runtime.typesystem.type;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeDefTableRow;
import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;

public class TypeFactory implements ITypeFactory {
    private final CLIFile _file;

    //region ITypeFactory
    @Override
    public IType create(CLITypeDefTableRow type) {
        throw new NotImplementedException();
    }
    //endregion

    public TypeFactory(CLIFile file) {
        _file = file;
    }
}
