package com.vztekoverflow.bacil.parser.signatures;

import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.runtime.types.ByRefWrapped;
import com.vztekoverflow.bacil.runtime.types.CustomMod;
import com.vztekoverflow.bacil.runtime.types.CustomModWrapped;
import com.vztekoverflow.bacil.runtime.types.Type;

import java.util.List;

import static com.vztekoverflow.bacil.parser.signatures.TypeSig.*;

/**
 * Class implementing parsing for Param and RetType, as specified in II.23.2.10 Param and II.23.2.11 RetType.
 *
 * RetType is identical to Param except for one extra possibility, that it can include the type VOID. This
 * is implemented by the allowVoid parameter.
 */
public class ParamSig {

    public static Type read(SignatureReader reader, boolean allowVoid, CLIComponent component)
    {
        boolean byRef = false;
        List<CustomMod> mods = CustomMod.readAll(reader);

        if (reader.peekUnsigned() == ELEMENT_TYPE_TYPEDBYREF) {
            reader.getUnsigned();
            return component.getBuiltinTypes().getTypedReferenceType();
        }

        if (allowVoid && reader.peekUnsigned() == ELEMENT_TYPE_VOID) {
            reader.getUnsigned();
            return component.getBuiltinTypes().getVoidType();
        }

        if (reader.peekUnsigned() == ELEMENT_TYPE_BYREF)
        {
            byRef = true;
            reader.getUnsigned();
        }

        Type type = TypeSig.read(reader, component);
        if(byRef)
        {
            type = new ByRefWrapped(type);
        }

        if(mods != null)
        {
            type = new CustomModWrapped(type, mods);
        }

        return type;
    }
}
