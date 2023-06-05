package com.vztekoverflow.cilostazol.meta;

import com.oracle.truffle.api.CompilerAsserts;
import com.vztekoverflow.cilostazol.context.ContextAccessImpl;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.CLIType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.TypeBase;

public final class Meta extends ContextAccessImpl {
    public Meta(CILOSTAZOLContext context) {
        super(context);
    }

    /*
    public final TypeBase<?> system_Object;
    public final TypeBase<?> system_Object_array;

    public final TypeBase<?> system_String;
    public final TypeBase<?> system_Type;

    public final CLIType _boolean;
    public final CLIType _char;
    public final CLIType _float;
    public final CLIType _int;
    public final CLIType _double;
    public final CLIType _long;
    public final CLIType _void;

    public Meta(CILOSTAZOLContext context) {
        super(context);
        CompilerAsserts.neverPartOfCompilation();

        // Give access to the partially-built Meta instance.
        context.setBootstrapMeta(this);

        // Core types.
        // Object and Class (+ Class fields) must be initialized before all other classes in order
        // to eagerly create the guest Class instances.
    }
    */
}