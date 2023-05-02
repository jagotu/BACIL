package com.vztekoverflow.cilostazol.runtime.typesystem.appdomain;

import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cilostazol.runtime.typesystem.assembly.IAssembly;

public interface IAppDomain {
    public IAssembly[] getAssemblies();
    public IAssembly getAssembly(AssemblyIdentity identity);
    public void loadAssembly(IAssembly assembly);
}
