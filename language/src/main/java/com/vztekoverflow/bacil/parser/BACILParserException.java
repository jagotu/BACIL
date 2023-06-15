package com.vztekoverflow.bacil.parser;

import com.vztekoverflow.bacil.BACILException;

/** A class for errors during parsing. */
public class BACILParserException extends BACILException {
  public BACILParserException(String message) {
    super(message);
  }
}
