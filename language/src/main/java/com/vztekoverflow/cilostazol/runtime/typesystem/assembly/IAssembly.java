package com.vztekoverflow.cilostazol.runtime.typesystem.assembly;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public interface IAssembly {
    public CLIFile getDefiningFile();
    public IComponent[] getComponents();
    public IType[] getLocalType();
}
