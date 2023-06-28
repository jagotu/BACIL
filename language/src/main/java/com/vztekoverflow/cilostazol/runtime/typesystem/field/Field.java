package com.vztekoverflow.cilostazol.runtime.typesystem.field;

import com.oracle.truffle.api.staticobject.StaticProperty;
import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.meta.SystemTypes;
import com.vztekoverflow.cilostazol.runtime.objectmodel.StaticObject;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitution;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.TypeBase;

public class Field extends StaticProperty implements IField {

  private final String name;
  private final IType type;
  private final boolean isStatic;
  private final boolean isInitOnly;
  private final boolean isLiteral;
  private final boolean isNotSerialized;
  private final boolean isSpecialName;
  private final int flags;
  private final CLIFile definingFile;
  private final TypeBase<?> declaringType;
  private final int slot;

  public Field(
      String name,
      IType type,
      boolean isStatic,
      boolean isInitOnly,
      boolean isLiteral,
      boolean isNotSerialized,
      boolean isSpecialName,
      int flags,
      CLIFile definingFile) {
    this.name = name;
    this.type = type;
    this.isStatic = isStatic;
    this.isInitOnly = isInitOnly;
    this.isLiteral = isLiteral;
    this.isNotSerialized = isNotSerialized;
    this.isSpecialName = isSpecialName;
    this.flags = flags;
    this.definingFile = definingFile;

    // TODO:
    declaringType = null;
    this.slot = -1;
  }

  @Override
  public CLIFile getDefiningFile() {
    return definingFile;
  }

  @Override
  public IType getType() {
    return type;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public IField substitute(ISubstitution<IType> substitution) {
    return new Field(
        name,
        substitution.substitute(type),
        isStatic,
        isInitOnly,
        isLiteral,
        isNotSerialized,
        isSpecialName,
        flags,
        definingFile);
  }

  @Override
  public IField getDefinition() {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public IField getConstructedFrom() {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public FieldVisibility getVisibility() {
    return FieldVisibility.fromFlags(flags);
  }

  @Override
  public boolean isInitOnly() {
    return isInitOnly;
  }

  @Override
  public boolean isSpecialName() {
    return isSpecialName;
  }

  @Override
  public boolean isNotSerialized() {
    return isNotSerialized;
  }

  @Override
  public boolean isLiteral() {
    return isLiteral;
  }

  @Override
  public boolean isStatic() {
    return isStatic;
  }

  @Override
  protected String getId() {
    return getName();
  }

  @Override
  public Class<?> getPropertyType() {
    // TODO: Remove
    if (type == null) {
      return int.class;
    }

    return switch (type.getKind()) {
      case Boolean -> boolean.class;
      case Char -> char.class;
      case Float -> float.class;
      case Double -> double.class;
      case Int -> int.class;
      case Long -> long.class;
      default -> StaticObject.class;
    };
  }

  @Override
  public boolean isFinal() {
    return false;
  }

  @Override
  public SystemTypes getKind() {
    return type.getKind();
  }

  @Override
  public final void setObjectValue(StaticObject obj, Object value) {
    // Call to StaticProperty!
    setObject(obj, value);
  }

  public final TypeBase<?> getDeclaringType() {
    return declaringType;
  }
}
