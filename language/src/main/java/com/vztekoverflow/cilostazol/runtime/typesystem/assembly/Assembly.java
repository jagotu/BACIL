package com.vztekoverflow.cilostazol.runtime.typesystem.assembly;

import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.Component;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.Type;

public class Assembly {
    private final Component[] _components = null;
    private final AssemblyIdentity _identity = null;

    public AssemblyIdentity getAssemblyIdentity() {
        return _identity;
    }

    public Type findLocalType(String namespace, String name) {
        //TODO
        return null;
    }
}
