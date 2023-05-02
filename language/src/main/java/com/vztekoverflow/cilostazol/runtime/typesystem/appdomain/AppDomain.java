package com.vztekoverflow.cilostazol.runtime.typesystem.appdomain;

import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.typesystem.assembly.Assembly;
import com.vztekoverflow.cilostazol.runtime.typesystem.assembly.IAssembly;

import java.util.ArrayList;

public class AppDomain implements IAppDomain {
    private final ArrayList<IAssembly> _assemblies;

    public AppDomain() {
        _assemblies = new ArrayList<>();
    }

    @Override
    public IAssembly[] getAssemblies() {
        return (IAssembly[]) _assemblies.toArray();
    }
}
