package com.vztekoverflow.cilostazol.runtime.typesystem.component;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.runtime.typesystem.appdomain.IAppDomain;
import com.vztekoverflow.cilostazol.runtime.typesystem.assembly.IAssembly;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public interface IComponent {
    public CLIFile getDefiningFile();
    public IAssembly getDefiningAssembly();
    public IType getLocalType(String namespace, String name);
    //Note: CIL modules also have identity which can be used in TypeRef table.
    // However, for simplicity, we will always ask assembly to find desired type(typename, namespace).
    // Assembly will probe all of its module to find the name.
}
