package com.vztekoverflow.cilostazol.runtime;

import com.vztekoverflow.cilostazol.meta.SystemTypes;
import com.vztekoverflow.cilostazol.runtime.symbols.TypeSymbol;

public abstract class ReferenceSymbol extends TypeSymbol {
  private final TypeSymbol underlyingTypeSymbol;

  public ReferenceSymbol(TypeSymbol underlyingTypeSymbol) {
    super(underlyingTypeSymbol.getDefiningModule(), SystemTypes.Int);
    this.underlyingTypeSymbol = underlyingTypeSymbol;
  }

  public TypeSymbol getUnderlyingTypeSymbol() {
    return underlyingTypeSymbol;
  }
}
