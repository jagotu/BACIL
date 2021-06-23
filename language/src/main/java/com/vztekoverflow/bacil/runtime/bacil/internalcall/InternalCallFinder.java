package com.vztekoverflow.bacil.runtime.bacil.internalcall;

import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLIMethodDefTableRow;
import com.vztekoverflow.bacil.runtime.BACILMethod;
import com.vztekoverflow.bacil.runtime.types.CLIType;
import com.vztekoverflow.bacil.runtime.types.Type;

/**
 * Class for finding an implementation of internalcall methods.
 */
public class InternalCallFinder {

    /**
     * Find an implementation for an internalcall method.
     * @param definingComponent the component that defines the internalcall method
     * @param method the internalcall method definition
     * @param type the type the method belongs to
     * @return found internalcall method or null
     */
    public static BACILMethod FindInternalCallMethod(CLIComponent definingComponent, CLIMethodDefTableRow method, Type type)
    {
        String methodName = method.getName().read(definingComponent.getStringHeap());
        if(type instanceof CLIType)
        {
            CLIType namedType = (CLIType) type;
            if(namedType.getNamespace().equals("System") && namedType.getName().equals("Math"))
            {
                if(methodName.equals("Sqrt"))
                {
                    return new MathSqrtMethod(definingComponent.getBuiltinTypes(), definingComponent.getLanguage(), type);
                } else if (methodName.equals("Abs"))
                {
                    return new MathAbsMethod(definingComponent.getBuiltinTypes(), definingComponent.getLanguage(), type);
                } else if (methodName.equals("Cos"))
                {
                    return new MathCosMethod(definingComponent.getBuiltinTypes(), definingComponent.getLanguage(), type);
                }
            } else if (namedType.getNamespace().equals("System.Runtime.CompilerServices") && namedType.getName().equals("RuntimeHelpers"))
            {
                if(methodName.equals("InitializeArray"))
                {
                    return new InitializeArrayMethod(definingComponent.getBuiltinTypes(), definingComponent.getLanguage(), type);
                }
            }
        }
        return null;
    }
}
