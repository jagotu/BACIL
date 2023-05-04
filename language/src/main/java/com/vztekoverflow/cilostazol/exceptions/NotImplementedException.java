package com.vztekoverflow.cilostazol.exceptions;

import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;

public class NotImplementedException extends CILOSTAZOLException {
    public NotImplementedException() {
        super(CILOSTAZOLBundle.message("cilostazol.exception.notImplemented"));
    }
}
