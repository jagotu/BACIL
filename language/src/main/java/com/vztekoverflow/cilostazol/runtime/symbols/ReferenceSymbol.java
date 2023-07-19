package com.vztekoverflow.cilostazol.runtime.symbols;

import com.vztekoverflow.cilostazol.nodes.CILOSTAZOLFrame;
import com.vztekoverflow.cilostazol.runtime.objectmodel.SystemTypes;

public abstract class ReferenceSymbol extends TypeSymbol {
  private final TypeSymbol underlyingTypeSymbol;

  public ReferenceSymbol(TypeSymbol underlyingTypeSymbol) {
    super(underlyingTypeSymbol.getDefiningModule(), CILOSTAZOLFrame.StackType.Int, SystemTypes.Int);
    this.underlyingTypeSymbol = underlyingTypeSymbol;
  }

  public TypeSymbol getUnderlyingTypeSymbol() {
    return underlyingTypeSymbol;
  }
}
