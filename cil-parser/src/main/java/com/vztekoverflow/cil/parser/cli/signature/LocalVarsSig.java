package com.vztekoverflow.cil.parser.cli.signature;

import com.vztekoverflow.cil.parser.cli.CLIFile;

public class LocalVarsSig {
  private static final String TYPE = "LocalVarSig";
  private final LocalVarSig[] _vars;

  public LocalVarsSig(LocalVarSig[] _vars) {
    this._vars = _vars;
  }

  public static LocalVarsSig read(SignatureReader reader, CLIFile file) {
    reader.assertUnsigned(7, TYPE);

    int count = reader.getUnsigned();
    LocalVarSig[] varTypes = new LocalVarSig[count];

    for (int i = 0; i < count; i++) {
      varTypes[i] = LocalVarSig.parse(reader, file);
    }

    return new LocalVarsSig(varTypes);
  }

  public int getVarsCount() {
    return _vars.length;
  }

  public LocalVarSig[] getVars() {
    return _vars;
  }
}
