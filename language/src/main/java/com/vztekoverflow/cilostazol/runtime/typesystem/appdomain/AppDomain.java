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

    //region IAppDomain
    @Override
    public IAssembly[] getAssemblies() {
        return (IAssembly[]) _assemblies.toArray();
    }

    @Override
    public IAssembly getAssembly(AssemblyIdentity identity) {
        IAssembly result = null;

        for (int i = 0; i < _assemblies.size() && result == null; i++) {
            if (_assemblies.get(i).getIdentity().resolvesRef(identity))
                result = _assemblies.get(i);
        }

        return result;
    }

    @Override
    public void loadAssembly(IAssembly assembly) {
        _assemblies.add(assembly);
    }
    //endregion
}
