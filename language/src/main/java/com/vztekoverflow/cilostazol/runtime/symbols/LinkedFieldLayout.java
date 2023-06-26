package com.vztekoverflow.cilostazol.runtime.symbols;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.staticobject.StaticShape;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.objectmodel.StaticField;
import com.vztekoverflow.cilostazol.runtime.objectmodel.StaticObject;

public final class LinkedFieldLayout {
  final StaticShape<StaticObject.StaticObjectFactory> instanceShape;
  final StaticShape<StaticObject.StaticObjectFactory> staticShape;

  // instance fields declared in the corresponding LinkedKlass (includes hidden fields)
  @CompilerDirectives.CompilationFinal(dimensions = 1) //
  final StaticField[] instanceFields;
  // static fields declared in the corresponding LinkedKlass (no hidden fields)
  @CompilerDirectives.CompilationFinal(dimensions = 1) //
  final StaticField[] staticFields;

  final int fieldTableLength;

  LinkedFieldLayout(CILOSTAZOLContext description, NamedTypeSymbol parserTypeSymbol, NamedTypeSymbol superClass) {
    StaticShape.Builder instanceBuilder = StaticShape.newBuilder(description.getLanguage());
    StaticShape.Builder staticBuilder = StaticShape.newBuilder(description.getLanguage());

    FieldCounter fieldCounter = new FieldCounter(parserTypeSymbol);
    int nextInstanceFieldIndex = 0;
    int nextStaticFieldIndex = 0;
    int nextInstanceFieldSlot = superClass == null ? 0 : superClass.getFields().length;
    int nextStaticFieldSlot = 0;

    staticFields = new StaticField[fieldCounter.staticFields];
    instanceFields = new StaticField[fieldCounter.instanceFields];

    for (FieldSymbol parserField : parserTypeSymbol.getFields()) {
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

    if (superClass == null) {
      instanceShape =
          instanceBuilder.build(StaticObject.class, StaticObject.StaticObjectFactory.class);
    } else {
      instanceShape = instanceBuilder.build(superClass.getShape(false));
    }
    staticShape = staticBuilder.build(StaticObject.class, StaticObject.StaticObjectFactory.class);
    fieldTableLength = nextInstanceFieldSlot;
  }

  private static void createAndRegisterLinkedField(
      FieldSymbol parserField,
      int slot,
      int index,
      StaticShape.Builder builder,
      StaticField[] linkedFields) {
    StaticField field = new StaticField(parserField);
    builder.property(field, field.getPropertyType(), storeAsFinal(parserField));
    linkedFields[index] = field;
  }

  private static boolean storeAsFinal(FieldSymbol field) {
    return field.isLiteral();
  }

  private static final class FieldCounter {
    // Includes hidden fields
    final int instanceFields;
    final int staticFields;

    FieldCounter(NamedTypeSymbol parserTypeSymbol) {
      int iFields = 0;
      int sFields = 0;
      for (FieldSymbol f : parserTypeSymbol.getFields()) {
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
