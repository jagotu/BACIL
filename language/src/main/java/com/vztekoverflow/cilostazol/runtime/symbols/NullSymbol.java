package com.vztekoverflow.cilostazol.runtime.symbols;

import com.vztekoverflow.cilostazol.nodes.CILOSTAZOLFrame;

public class NullSymbol extends TypeSymbol {

  public NullSymbol() {
    super(null, CILOSTAZOLFrame.StackType.Object);
  }
}
