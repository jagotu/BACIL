package com.vztekoverflow.cilostazol.runtime.typesystem.appdomain;

import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cilostazol.context.ContextAccess;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.typesystem.assembly.IAssembly;

public interface IAppDomain extends ContextAccess {
    IAssembly[] getAssemblies();
    IAssembly getAssembly(AssemblyIdentity identity);
    void loadAssembly(IAssembly assembly);
    CILOSTAZOLContext getContext();
}
