package com.vztekoverflow.bacil.runtime.bacil;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.BACILLanguage;
import com.vztekoverflow.bacil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.builtin.BuiltinTypes;

/**
 * Class representing a generic component with an assembly identity and types.
 */
public abstract class BACILComponent {

    @CompilerDirectives.CompilationFinal
    private BuiltinTypes builtinTypes;

    private final BACILLanguage language;

    public BACILComponent(BACILLanguage language) {
        this.language = language;
    }

    /**
     * Set the resolved builtinTypes. Used to break the dependency initialization cycle.
     */
    public void setBuiltinTypes(BuiltinTypes builtinTypes) {
        CompilerAsserts.neverPartOfCompilation();
        this.builtinTypes = builtinTypes;
    }

    /**
     * Get the resolved builtinTypes. Used to break the dependency initialization cycle.
     */
    public BuiltinTypes getBuiltinTypes() {
        return builtinTypes;
    }

    /**
     * Get the identity of this assembly, including name and version.
     */
    public abstract AssemblyIdentity getAssemblyIdentity();

    /**
     * Get a type defined by this component.
     */
    public abstract Type findLocalType(String namespace, String name);

}
