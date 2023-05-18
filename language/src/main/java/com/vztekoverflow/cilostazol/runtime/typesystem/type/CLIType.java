package com.vztekoverflow.cilostazol.runtime.typesystem.type;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.CLIComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;

public abstract class CLIType implements IType{

    public abstract CLIComponent getCLIComponent();

    public CLIFile getDefiningFile()
    {
        return getCLIComponent().getDefiningFile();
    }

    @Override
    public IComponent getDefiningComponent() {
        return getCLIComponent();
    }
}
