package com.vztekoverflow.cil.parser.cli.signature;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import java.util.List;

public class LocalVarSig {
  public static final byte ELEMENT_TYPE_TYPEDBYREF = 0x16;
  public static final byte ELEMENT_TYPE_PINNED = 0x45;
  // region Constants
  private static final byte ELEMENT_TYPE_BYREF = 0x10;
  // endregion
  private final boolean _pinned;
  private final boolean _byRef;
  private final CustomMod[] _mods;
  private final TypeSig _typeSig;

  private LocalVarSig(boolean _pinned, boolean _byRef, CustomMod[] _mods, TypeSig _typeSig) {
    this._pinned = _pinned;
    this._byRef = _byRef;
    this._mods = _mods;
    this._typeSig = _typeSig;
  }

  public static LocalVarSig parse(SignatureReader reader, CLIFile file) {
    if (reader.peekUnsigned() == ELEMENT_TYPE_TYPEDBYREF) {
      return new LocalVarSig(false, false, null, TypeSig.read(reader, file));
    }

    boolean pinned = false;
    boolean byRef = false;

    final CustomMod[] mods;
    final List<CustomMod> modsL = CustomMod.readAll(reader);
    mods = (modsL != null) ? (CustomMod[]) modsL.toArray(new CustomMod[0]) : null;

    if (reader.peekUnsigned() == ELEMENT_TYPE_PINNED) {
      pinned = true;
      reader.getUnsigned();
    }

    if (reader.peekUnsigned() == ELEMENT_TYPE_BYREF) {
      byRef = true;
      reader.getUnsigned();
    }

    TypeSig type = TypeSig.read(reader, file);
    return new LocalVarSig(pinned, byRef, mods, type);
  }

  public boolean isPinned() {
    return _pinned;
  }

  public boolean isByRef() {
    return _byRef;
  }

  public TypeSig getTypeSig() {
    return _typeSig;
  }
}
