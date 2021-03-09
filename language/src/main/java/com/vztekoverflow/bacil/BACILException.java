package com.vztekoverflow.bacil;

public abstract class BACILException extends RuntimeException{
    public BACILException() {
    }

    public BACILException(String message) {
        super(message);
    }

    public BACILException(String message, Throwable cause) {
        super(message, cause);
    }

    public BACILException(Throwable cause) {
        super(cause);
    }

    public BACILException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
