package com.vztekoverflow.cil.parser.cli.signature;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import java.util.List;

public class ParamSig {
  // region Constants
  private static final byte ELEMENT_TYPE_BYREF = 0x10;
  // endregion

  private final CustomMod[] _mods;
  private final TypeSig _typeSig;
  private final boolean _byRef;

  public ParamSig(CustomMod[] mods, TypeSig typeSig, boolean byRef) {
    _mods = mods;
    _typeSig = typeSig;
    _byRef = byRef;
  }

  public static ParamSig parse(SignatureReader reader, CLIFile file, boolean allowVoid) {
    boolean byRef = false;
    final CustomMod[] mods;
    final List<CustomMod> modsL = CustomMod.readAll(reader);
    mods = (modsL != null) ? (CustomMod[]) modsL.toArray(new CustomMod[0]) : null;

    if (reader.peekUnsigned() == ELEMENT_TYPE_BYREF) {
      byRef = true;
      reader.getUnsigned();
    }

    final TypeSig type = TypeSig.read(reader, file);

    return new ParamSig(mods, type, byRef);
  }

  public boolean isByRef() {
    return _byRef;
  }

  public TypeSig getTypeSig() {
    return _typeSig;
  }
}
