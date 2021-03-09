package com.vztekoverflow.bacil.runtime.bacil;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.BACILLanguage;
import com.vztekoverflow.bacil.runtime.BACILMethod;
import com.vztekoverflow.bacil.runtime.types.CustomMod;
import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.builtin.BuiltinTypes;

import java.util.List;

public class BACILEnvironmentType extends Type {

    private final Type directBaseClass;

    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private final JavaMethod[] methods;

    public long timerStart = 0;

    public BACILEnvironmentType(BuiltinTypes builtinTypes, BACILLanguage language) {
        directBaseClass = builtinTypes.getObjectType();

        methods = new JavaMethod[] {
                new BACILGetTicksMethod(builtinTypes, language, this),
                new BACILStartTimerMethod(builtinTypes, language, this)
        };
    }

    @Override
    public Type getDirectBaseClass() {
        return directBaseClass;
    }

    @Override
    public BACILMethod getMemberMethod(String name, byte[] signature) {
        for (JavaMethod method : methods)
        {
            if(name.equals(method.getName()))
            {
                return method;
            }
        }

        CompilerDirectives.transferToInterpreterAndInvalidate();
        throw new BACILInternalError("No such method on BACILConsole: " + name);
    }

    @Override
    public boolean isByRef() {
        return false;
    }

    @Override
    public boolean isPinned() {
        return false;
    }

    @Override
    public List<CustomMod> getMods() {
        return null;
    }
}
