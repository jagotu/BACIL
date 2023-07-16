package com.vztekoverflow.cilostazol.runtime.symbols;

import com.vztekoverflow.cilostazol.meta.SystemTypes;

public class NullSymbol extends TypeSymbol {

  public NullSymbol() {
    super(null, SystemTypes.Object);
  }
}
