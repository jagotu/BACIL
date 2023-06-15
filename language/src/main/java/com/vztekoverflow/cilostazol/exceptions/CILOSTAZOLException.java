package com.vztekoverflow.cilostazol.exceptions;

/** A generic runtime exception for an error that happened in CILOSTAZOL. */
public abstract class CILOSTAZOLException extends RuntimeException {
  public CILOSTAZOLException() {}

  public CILOSTAZOLException(String message) {
    super(message);
  }

  public CILOSTAZOLException(String message, Throwable cause) {
    super(message, cause);
  }

  public CILOSTAZOLException(Throwable cause) {
    super(cause);
  }

  public CILOSTAZOLException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
