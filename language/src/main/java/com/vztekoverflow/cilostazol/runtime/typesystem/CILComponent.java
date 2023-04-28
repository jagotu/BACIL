package com.vztekoverflow.cilostazol.runtime.typesystem;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.Type;
import org.graalvm.polyglot.io.ByteSequence;

public class CILComponent extends Component {
    private final CLIComponent _cliComponent = null;
    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private final Type[] _localDefTypes = null;

    private CILComponent(){}

    public static CILComponent parse(ByteSequence bytes) {
        return null;
    }

    @Override
    public AssemblyIdentity getAssemblyIdentity() {
        return null;
    }

    @Override
    public Type findLocalType(String namespace, String name) {
        return null;
    }
}
