package com.vztekoverflow.cilostazol.runtime.typesystem.component;

import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.Type;

public abstract class Component {

    /**
     * Get the identity of this assembly, including name and version.
     */
    public abstract AssemblyIdentity getAssemblyIdentity();

    /**
     * Get a type defined by this component.
     */
    public abstract Type findLocalType(String namespace, String name);
}
