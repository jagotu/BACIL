package com.vztekoverflow.cil.parser.cli.signature;

import com.vztekoverflow.cil.parser.cli.CLIFile;

public class MethodDefSig {
  private final MethodDefFlags _flags;
  private final int _genParamCount;
  private final RetTypeSig _retType;
  private final ParamSig[] _params;

  public MethodDefSig(
      MethodDefFlags flags, int _genParamCount, RetTypeSig _retType, ParamSig[] _params) {
    _flags = flags;
    this._genParamCount = _genParamCount;
    this._retType = _retType;
    this._params = _params;
  }

  public static MethodDefSig parse(SignatureReader reader, CLIFile file) {
    MethodDefFlags flags = new MethodDefFlags(reader.getUnsigned());

    int genParamCount = 0;
    if (flags.hasFlag(MethodDefFlags.Flag.GENERIC)) {
      genParamCount = reader.getUnsigned();
    }

    final int count = reader.getUnsigned();
    final RetTypeSig retType = RetTypeSig.parse(reader, file);
    final ParamSig[] paramTypes = new ParamSig[count];
    for (int i = 0; i < count; i++) {
      paramTypes[i] = ParamSig.parse(reader, file, false);
    }

    return new MethodDefSig(flags, genParamCount, retType, paramTypes);
  }

  public MethodDefFlags getMethodDefFlags() {
    return _flags;
  }

  public int getGenParamCount() {
    return _genParamCount;
  }

  public ParamSig[] getParams() {
    return _params;
  }

  public RetTypeSig getRetType() {
    return _retType;
  }
}
