package com.vztekoverflow.cilostazol.meta;

import com.oracle.truffle.api.CompilerAsserts;
import com.vztekoverflow.cilostazol.context.ContextAccessImpl;
import com.vztekoverflow.cilostazol.objectmodel.ArrayKlass;
import com.vztekoverflow.cilostazol.objectmodel.ObjectKlass;
import com.vztekoverflow.cilostazol.objectmodel.ValueKlass;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;

public final class Meta extends ContextAccessImpl {
    public final ObjectKlass system_Object;
    public final ArrayKlass system_Object_array;

    public final ObjectKlass system_String;
    public final ObjectKlass system_Type;

    public final ValueKlass _boolean;
    public final ValueKlass _char;
    public final ValueKlass _float;
    public final ValueKlass _int;
    public final ValueKlass _double;
    public final ValueKlass _long;
    public final ValueKlass _void;

    public Meta(CILOSTAZOLContext context) {
        super(context);
        CompilerAsserts.neverPartOfCompilation();

        // Give access to the partially-built Meta instance.
        context.setBootstrapMeta(this);

        // Core types.
        // Object and Class (+ Class fields) must be initialized before all other classes in order
        // to eagerly create the guest Class instances.

        // TODO:
        system_Object = new ObjectKlass();

        system_Object_array = system_Object.array();

        // TODO:
        system_String = new ObjectKlass();
        // TODO:
        system_Type = new ObjectKlass();

        _boolean = new ValueKlass(context, SystemTypes.Boolean);
        _char = new ValueKlass(context, SystemTypes.Char);
        _float = new ValueKlass(context, SystemTypes.Float);
        _int = new ValueKlass(context, SystemTypes.Int);
        _double = new ValueKlass(context, SystemTypes.Double);
        _long = new ValueKlass(context, SystemTypes.Long);
        _void = new ValueKlass(context, SystemTypes.Void);
    }
}