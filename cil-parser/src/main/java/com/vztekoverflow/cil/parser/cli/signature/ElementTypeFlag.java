package com.vztekoverflow.cil.parser.cli.signature;

import com.vztekoverflow.cil.parser.CILParserException;

public class ElementTypeFlag {
  private final int _flag;

  public ElementTypeFlag(int flags) {
    _flag = flags;
  }

  public ElementTypeFlag.Flag getFlag() {
    for (var el : Flag.values()) {
      if (el.code == _flag) return el;
    }

    throw new CILParserException();
  }

  public enum Flag {
    ELEMENT_TYPE_END(0x00),
    ELEMENT_TYPE_VOID(0x01),
    ELEMENT_TYPE_BOOLEAN(0x02),
    ELEMENT_TYPE_CHAR(0x03),
    ELEMENT_TYPE_I1(0x04),
    ELEMENT_TYPE_U1(0x05),
    ELEMENT_TYPE_I2(0x06),
    ELEMENT_TYPE_U2(0x07),
    ELEMENT_TYPE_I4(0x08),
    ELEMENT_TYPE_U4(0x09),
    ELEMENT_TYPE_I8(0x0a),
    ELEMENT_TYPE_U8(0x0b),
    ELEMENT_TYPE_R4(0x0c),
    ELEMENT_TYPE_R8(0x0d),
    ELEMENT_TYPE_STRING(0x0e),
    ELEMENT_TYPE_PTR(0x0f),
    ELEMENT_TYPE_BYREF(0x10),
    ELEMENT_TYPE_VALUETYPE(0x11),
    ELEMENT_TYPE_CLASS(0x12),
    ELEMENT_TYPE_VAR(0x13),
    ELEMENT_TYPE_ARRAY(0x14),
    ELEMENT_TYPE_GENERICINST(0x15),
    ELEMENT_TYPE_TYPEDBYREF(0x16),
    ELEMENT_TYPE_I(0x18),
    ELEMENT_TYPE_U(0x19),
    ELEMENT_TYPE_FNPTR(0x1b),
    ELEMENT_TYPE_OBJECT(0x1c),
    ELEMENT_TYPE_SZARRAY(0x1d),
    ELEMENT_TYPE_MVAR(0x1e);

    public final int code;

    Flag(int code) {
      this.code = code;
    }
  }
}
