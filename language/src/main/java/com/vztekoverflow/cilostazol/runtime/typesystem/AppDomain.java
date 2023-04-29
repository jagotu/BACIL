package com.vztekoverflow.cilostazol.runtime.typesystem;

import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.typesystem.assembly.Assembly;

import java.util.ArrayList;

public class AppDomain {
    private final ArrayList<Assembly> _assemblies;
    private final CILOSTAZOLContext _context;

    public AppDomain(CILOSTAZOLContext context) {
        _assemblies = new ArrayList<>();
        _context = context;
    }

    public void loadAssembly(Assembly assembly) {
        _assemblies.add(assembly);
    }

    public Assembly getAssembly(AssemblyIdentity identity) {
        for (Assembly assembly: _assemblies) {
            assert assembly.getAssemblyIdentity() != null;
            if(assembly.getAssemblyIdentity().resolvesRef(identity))
            {
                return assembly;
            }
        }

        //Use context to find the assembly
        return null;
    }
}
