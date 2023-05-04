package com.vztekoverflow.cilostazol.runtime.typesystem.assembly;

import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.runtime.typesystem.appdomain.IAppDomain;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public interface IAssembly {
    public CLIFile getDefiningFile();
    public IComponent[] getComponents();
    public IType getLocalType(String namespace, String name, IAppDomain appDomain);
    public AssemblyIdentity getIdentity();
}
