package com.vztekoverflow.cilostazol.runtime.typesystem.component;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public class CLIComponent implements IComponent {
    private final CLIFile _cliFile = null;

    @Override
    public CLIFile getDefiningFile() {
        return _cliFile;
    }

    @Override
    public IType getLocalType(String namespace, String name) {
        return null;
    }
}
