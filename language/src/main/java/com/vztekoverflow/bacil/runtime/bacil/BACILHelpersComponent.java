package com.vztekoverflow.bacil.runtime.bacil;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.BACILLanguage;
import com.vztekoverflow.bacil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.builtin.BuiltinTypes;

/**
 * Implementation of the BACILHelpers component, used for accessing system resources that can't be accessed
 * through the .NET methods because of incomplete implementation.
 */
public class BACILHelpersComponent extends BACILComponent {

    static AssemblyIdentity assemblyIdentity = new AssemblyIdentity((short)0, (short)0, (short)0, (short)0, "BACILHelpers");

    private final BACILConsoleType consoleType;
    private final BACILEnvironmentType environmentType;

    public BACILHelpersComponent(BuiltinTypes builtinTypes, BACILLanguage language) {
        super(language);
        consoleType = new BACILConsoleType(builtinTypes, language);
        environmentType = new BACILEnvironmentType(builtinTypes, language);
    }

    @Override
    public AssemblyIdentity getAssemblyIdentity() {
        return assemblyIdentity;
    }

    @Override
    public Type findLocalType(String namespace, String name) {
        if(name.equals("BACILConsole"))
            return consoleType;
        else if(name.equals("BACILEnvironment"))
            return environmentType;

        CompilerDirectives.transferToInterpreterAndInvalidate();
        throw new BACILInternalError("Type not found in BACILHelpers: " + name);
    }
}
