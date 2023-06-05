package com.vztekoverflow.cilostazol.exceptions;

import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;

public class NegativeArraySizeException extends CILOSTAZOLException {
    public NegativeArraySizeException() {
        super(CILOSTAZOLBundle.message("cilostazol.exception.negativeArraySizeException"));
    }
}
