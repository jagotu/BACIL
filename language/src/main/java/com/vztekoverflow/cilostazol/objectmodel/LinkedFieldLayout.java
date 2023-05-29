package com.vztekoverflow.cilostazol.objectmodel;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.staticobject.StaticShape;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.typesystem.field.IField;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.CLIType;

public final class LinkedFieldLayout {
    final StaticShape<StaticObject.StaticObjectFactory> instanceShape;
    final StaticShape<StaticObject.StaticObjectFactory> staticShape;

    // instance fields declared in the corresponding LinkedKlass (includes hidden fields)
    @CompilerDirectives.CompilationFinal(dimensions = 1) //
    final LinkedField[] instanceFields;
    // static fields declared in the corresponding LinkedKlass (no hidden fields)
    @CompilerDirectives.CompilationFinal(dimensions = 1) //
    final LinkedField[] staticFields;

    final int fieldTableLength;

    LinkedFieldLayout(CILOSTAZOLContext description, CLIType parserKlass, CLIType superKlass) {
        StaticShape.Builder instanceBuilder = StaticShape.newBuilder(description.getLanguage());
        StaticShape.Builder staticBuilder = StaticShape.newBuilder(description.getLanguage());

        FieldCounter fieldCounter = new FieldCounter(parserKlass);
        int nextInstanceFieldIndex = 0;
        int nextStaticFieldIndex = 0;
        int nextInstanceFieldSlot = superKlass == null ? 0 : superKlass.getFields().length;
        int nextStaticFieldSlot = 0;

        staticFields = new LinkedField[fieldCounter.staticFields];
        instanceFields = new LinkedField[fieldCounter.instanceFields];

        for (IField parserField : parserKlass.getFields()) {
            if (parserField.isStatic()) {
                createAndRegisterLinkedField(parserKlass, parserField, nextStaticFieldSlot++, nextStaticFieldIndex++, staticBuilder, staticFields);
            } else {
                createAndRegisterLinkedField(parserKlass, parserField, nextInstanceFieldSlot++, nextInstanceFieldIndex++, instanceBuilder, instanceFields);
            }
        }

        if (superKlass == null) {
            instanceShape = instanceBuilder.build(StaticObject.class, StaticObject.StaticObjectFactory.class);
        } else {
            instanceShape = instanceBuilder.build(superKlass.getShape(false));
        }
        staticShape = staticBuilder.build(StaticObject.class, StaticObject.StaticObjectFactory.class);
        fieldTableLength = nextInstanceFieldSlot;
    }

    private static void createAndRegisterLinkedField(CLIType parserKlass, IField parserField, int slot, int index, StaticShape.Builder builder, LinkedField[] linkedFields) {
        LinkedField field = new LinkedField(parserField, slot);
        builder.property(field, parserField.getPropertyType(), storeAsFinal(parserField));
        linkedFields[index] = field;
    }

    private static boolean storeAsFinal(IField field) {
        return field.isFinal();
    }

    private static final class FieldCounter {
        // Includes hidden fields
        final int instanceFields;
        final int staticFields;

        FieldCounter(CLIType parserKlass) {
            int iFields = 0;
            int sFields = 0;
            for (IField f : parserKlass.getFields()) {
                if (f.isStatic()) {
                    sFields++;
                } else {
                    iFields++;
                }
            }

            instanceFields = iFields;
            staticFields = sFields;
        }
    }
}
