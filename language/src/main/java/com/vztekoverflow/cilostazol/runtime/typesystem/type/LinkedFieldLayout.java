package com.vztekoverflow.cilostazol.runtime.typesystem.type;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.staticobject.StaticShape;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.objectmodel.StaticObject;
import com.vztekoverflow.cilostazol.runtime.typesystem.field.Field;
import com.vztekoverflow.cilostazol.runtime.typesystem.field.IField;

public final class LinkedFieldLayout {
  final StaticShape<StaticObject.StaticObjectFactory> instanceShape;
  final StaticShape<StaticObject.StaticObjectFactory> staticShape;

  // instance fields declared in the corresponding LinkedKlass (includes hidden fields)
  @CompilerDirectives.CompilationFinal(dimensions = 1) //
  final Field[] instanceFields;
  // static fields declared in the corresponding LinkedKlass (no hidden fields)
  @CompilerDirectives.CompilationFinal(dimensions = 1) //
  final Field[] staticFields;

  final int fieldTableLength;

  LinkedFieldLayout(CILOSTAZOLContext description, CLIType parserKlass, TypeBase<?> superKlass) {
    StaticShape.Builder instanceBuilder = StaticShape.newBuilder(description.getLanguage());
    StaticShape.Builder staticBuilder = StaticShape.newBuilder(description.getLanguage());

    FieldCounter fieldCounter = new FieldCounter(parserKlass);
    int nextInstanceFieldIndex = 0;
    int nextStaticFieldIndex = 0;
    int nextInstanceFieldSlot = superKlass == null ? 0 : superKlass.getFields().length;
    int nextStaticFieldSlot = 0;

    staticFields = new Field[fieldCounter.staticFields];
    instanceFields = new Field[fieldCounter.instanceFields];

    for (IField parserField : parserKlass.getFields()) {
      if (parserField.isStatic()) {
        createAndRegisterLinkedField(
            parserField,
            nextStaticFieldSlot++,
            nextStaticFieldIndex++,
            staticBuilder,
            staticFields);
      } else {
        createAndRegisterLinkedField(
            parserField,
            nextInstanceFieldSlot++,
            nextInstanceFieldIndex++,
            instanceBuilder,
            instanceFields);
      }
    }

    if (superKlass == null) {
      instanceShape =
          instanceBuilder.build(StaticObject.class, StaticObject.StaticObjectFactory.class);
    } else {
      instanceShape = instanceBuilder.build(superKlass.getShape(false));
    }
    staticShape = staticBuilder.build(StaticObject.class, StaticObject.StaticObjectFactory.class);
    fieldTableLength = nextInstanceFieldSlot;
  }

  private static void createAndRegisterLinkedField(
      IField parserField, int slot, int index, StaticShape.Builder builder, Field[] linkedFields) {
    builder.property((Field) parserField, parserField.getPropertyType(), storeAsFinal(parserField));
    linkedFields[index] = (Field) parserField;
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
