package com.vztekoverflow.cilostazol.runtime.typesystem;

import com.vztekoverflow.bacil.runtime.bacil.BACILComponent;
import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cilostazol.runtime.typesystem.assembly.Assembly;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.Component;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.Map;

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
        throw new NotImplementedException();
    }
}
