package com.vztekoverflow.cilostazol.runtime.typesystem.assembly;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public class Assembly implements IAssembly {
    private final IComponent[] _components = null;
    private final CLIFile _file = null;

    @Override
    public CLIFile getDefiningFile() {
        return _file;
    }

    public IComponent[] getComponents() {return _components;}

    @Override
    public IType[] getLocalType() {
        return new IType[0];
    }
}
