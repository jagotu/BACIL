package com.vztekoverflow.bacil.runtime.bacil;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.BACILLanguage;
import com.vztekoverflow.bacil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.builtin.BuiltinTypes;

public abstract class BACILComponent {

    @CompilerDirectives.CompilationFinal
    private BuiltinTypes builtinTypes;

    private final BACILLanguage language;

    public BACILComponent(BACILLanguage language) {
        this.language = language;
    }

    public void setBuiltinTypes(BuiltinTypes builtinTypes) {
        CompilerAsserts.neverPartOfCompilation();
        this.builtinTypes = builtinTypes;
    }

    public BuiltinTypes getBuiltinTypes() {
        return builtinTypes;
    }

    public abstract AssemblyIdentity getAssemblyIdentity();

    public abstract Type findLocalType(String namespace, String name);

}
