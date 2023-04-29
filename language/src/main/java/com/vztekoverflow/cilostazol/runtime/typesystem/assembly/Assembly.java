package com.vztekoverflow.cilostazol.runtime.typesystem.assembly;

import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.Component;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.Type;
import org.graalvm.polyglot.Source;

public class Assembly {
    private final Component[] _components;
    private final AssemblyIdentity _identity;
    private final CLIFile _file;

    private Assembly(CLIFile file, Component[] components) {
        _components = components;
        _file = file;
        _identity = _file.getAssemblyIdentity();
    }

    public AssemblyIdentity getAssemblyIdentity() {
        return _identity;
    }

    public static Assembly parseAssembly(Source dllSource) {
        //todo
        return null;
    }

    public Type findLocalType(String namespace, String name) {
        //TODO
        return null;
    }
}
