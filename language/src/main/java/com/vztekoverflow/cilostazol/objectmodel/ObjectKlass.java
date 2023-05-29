package com.vztekoverflow.cilostazol.objectmodel;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.typesystem.field.Field;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.CLIType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

import java.lang.reflect.Modifier;

public class ObjectKlass extends Klass {

    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private final Field[] fieldTable;

    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private final Field[] staticFieldTable;

    private final int localFieldTableIndex;

    public ObjectKlass(CILOSTAZOLContext context, CLIType parsedType, ObjectKlass superKlass, ObjectKlass[] superInterfaces, StaticObject classLoader) {
        super(context, 0);

        Field[] skFieldTable = superKlass != null ? superKlass.getInitialFieldTable() : new Field[0];
        LinkedField[] lkInstanceFields = parsedType.getInstanceFields();
        LinkedField[] lkStaticFields = parsedType.getStaticFields();

        fieldTable = new Field[skFieldTable.length + lkInstanceFields.length];
        staticFieldTable = new Field[lkStaticFields.length];

        assert fieldTable.length == parsedType.getFields().length;
        System.arraycopy(skFieldTable, 0, fieldTable, 0, skFieldTable.length);
        localFieldTableIndex = skFieldTable.length;
        for (int i = 0; i < lkInstanceFields.length; i++) {
            // TODO:
            Field instanceField = null;
            fieldTable[localFieldTableIndex + i] = instanceField;
        }
        for (int i = 0; i < lkStaticFields.length; i++) {
            Field staticField;
            LinkedField lkField = lkStaticFields[i];
            // TODO:
            staticField = null;
            staticFieldTable[i] = staticField;
        }
    }

    public Field[] getInitialFieldTable() {
        return fieldTable;
    }

    public Field[] getInitialStaticFields() {
        return staticFieldTable;
    }
}
