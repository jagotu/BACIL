package com.vztekoverflow.bacil.runtime.bacil.internalcall;

import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLIMethodDefTableRow;
import com.vztekoverflow.bacil.runtime.BACILMethod;
import com.vztekoverflow.bacil.runtime.types.NamedType;
import com.vztekoverflow.bacil.runtime.types.Type;

public class InternalCallFinder {

    public static BACILMethod FindInternalCallMethod(CLIComponent definingComponent, CLIMethodDefTableRow method, Type type)
    {
        String methodName = method.getName().read(definingComponent.getStringHeap());
        if(type instanceof NamedType)
        {
            NamedType namedType = (NamedType) type;
            if(namedType.getNamespace().equals("System") && namedType.getName().equals("Math"))
            {
                if(methodName.equals("Sqrt"))
                {
                    return new MathSqrtMethod(definingComponent.getBuiltinTypes(), definingComponent.getLanguage(), type);
                }
            }
        }
        return null;
    }
}
