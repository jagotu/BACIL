package com.vztekoverflow.cilostazol.exceptions;

import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;

public class InstantiationError extends CILOSTAZOLException {
  public InstantiationError() {
    super(CILOSTAZOLBundle.message("cilostazol.exception.instantiationError"));
  }
}
