package com.vztekoverflow.cilostazol.runtime.typesystem.assembly;

import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.context.ContextAccess;
import com.vztekoverflow.cilostazol.runtime.typesystem.appdomain.IAppDomain;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public interface IAssembly extends ContextAccess {
    //TODO: CLIFIle in IAssembly ... get rid of it, or abandon IAssembly completely?
    CLIFile getDefiningFile();
    IComponent[] getComponents();
    AssemblyIdentity getIdentity();
    void setAppDomain(IAppDomain appdomain);
    IAppDomain getAppDomain();
    IType getLocalType(String namespace, String name);
}
