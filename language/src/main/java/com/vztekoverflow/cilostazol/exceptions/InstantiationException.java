package com.vztekoverflow.cilostazol.exceptions;

import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;

public class InstantiationException extends CILOSTAZOLException {
    public InstantiationException() {
        super(CILOSTAZOLBundle.message("cilostazol.exception.instantiationException"));
    }
}
