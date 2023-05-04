package com.vztekoverflow.cil.parser;

/**
 * A class for errors during parsing.
 */
public class CILParserException extends RuntimeException{
    public CILParserException() {
    }

    public CILParserException(String message) {
        super(message);
    }

    public CILParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public CILParserException(Throwable cause) {
        super(cause);
    }

    public CILParserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
