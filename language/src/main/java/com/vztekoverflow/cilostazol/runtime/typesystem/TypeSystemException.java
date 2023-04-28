package com.vztekoverflow.cilostazol.runtime.typesystem;

import com.vztekoverflow.cilostazol.CILOSTAZOLException;

public class TypeSystemException extends CILOSTAZOLException {
    public TypeSystemException() {
    }

    public TypeSystemException(String message) {
        super(message);
    }

    public TypeSystemException(String message, Throwable cause) {
        super(message, cause);
    }

    public TypeSystemException(Throwable cause) {
        super(cause);
    }

    public TypeSystemException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
