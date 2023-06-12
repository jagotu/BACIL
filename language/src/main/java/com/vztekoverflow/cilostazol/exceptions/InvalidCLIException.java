package com.vztekoverflow.cilostazol.exceptions;

import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;

public class InvalidCLIException extends CILOSTAZOLException {
  public InvalidCLIException() {
    super(CILOSTAZOLBundle.message("cilostazol.exception.invalid.CIL"));
  }
}
