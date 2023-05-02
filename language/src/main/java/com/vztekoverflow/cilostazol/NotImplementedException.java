package com.vztekoverflow.cilostazol;

public class NotImplementedException extends CILOSTAZOLException{
    public NotImplementedException() {
        super(CILOSTAZOLBundle.message("cilostazol.exception.notImplemented"));
    }
}
