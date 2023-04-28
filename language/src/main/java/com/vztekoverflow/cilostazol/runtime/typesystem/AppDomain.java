package com.vztekoverflow.cilostazol.runtime.typesystem;

import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cilostazol.runtime.typesystem.assembly.Assembly;

import java.util.ArrayList;

public class AppDomain {
    private final ArrayList<Assembly> _assemblies = null;

    public Assembly getAssembly(AssemblyIdentity identity) {
        for (Assembly assembly: _assemblies) {
            assert assembly.getAssemblyIdentity() != null;
            if(assembly.getAssemblyIdentity().resolvesRef(identity))
            {
                return assembly;
            }
        }

        //TODO: finding assembly with help of Context
        return null;
    }
}
