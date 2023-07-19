package com.vztekoverflow.cilostazol.runtime.symbols;

import com.vztekoverflow.cilostazol.nodes.CILOSTAZOLFrame;
import com.vztekoverflow.cilostazol.runtime.objectmodel.SystemTypes;

public class NullSymbol extends TypeSymbol {

  public NullSymbol() {
    super(null, CILOSTAZOLFrame.StackType.Object, SystemTypes.Object);
  }
}
