package com.vztekoverflow.cilostazol.runtime.objectmodel;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.interop.TruffleObject;
import com.vztekoverflow.cilostazol.runtime.symbols.NamedTypeSymbol;

public class StaticObject implements TruffleObject, Cloneable {
  public static final StaticObject[] EMPTY_ARRAY = new StaticObject[0];
  public static final StaticObject NULL = new StaticObject(null);
  private final NamedTypeSymbol typeSymbol;

  protected StaticObject(NamedTypeSymbol typeSymbol) {
    this.typeSymbol = typeSymbol;
  }

  public static boolean isNull(StaticObject object) {
    assert object != null;
    assert (object.getTypeSymbol() != null) || object == NULL
        : "Klass can only be null for NULL object";
    return object.getTypeSymbol() == null;
  }

  @Override
  @CompilerDirectives.TruffleBoundary
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public final NamedTypeSymbol getTypeSymbol() {
    return typeSymbol;
  }

  public final boolean isArray() {
    return !isNull(this) && getTypeSymbol().isArray();
  }

  public interface StaticObjectFactory {
    StaticObject create(NamedTypeSymbol type);
  }
}
