package com.vztekoverflow.cilostazol.runtime.typesystem.assembly;

import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.runtime.typesystem.appdomain.IAppDomain;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public interface IAssembly {
    //TODO: CLIFIle in IAssembly ... get rid of it, or abandon IAssembly completely?
    public CLIFile getDefiningFile();
    public IComponent[] getComponents();
    public AssemblyIdentity getIdentity();
    public void setAppDomain(IAppDomain appdomain);
    public IAppDomain getAppDomain();

    public IType getLocalType(String namespace, String name);
}
