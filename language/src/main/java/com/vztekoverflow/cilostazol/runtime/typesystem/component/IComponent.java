package com.vztekoverflow.cilostazol.runtime.typesystem.component;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.runtime.typesystem.appdomain.IAppDomain;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public interface IComponent {
    public CLIFile getDefiningFile();
    public IType getLocalType(String namespace, String name, IAppDomain appDomain);
}
