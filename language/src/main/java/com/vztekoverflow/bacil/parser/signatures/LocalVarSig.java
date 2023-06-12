package com.vztekoverflow.bacil.parser.signatures;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.runtime.types.*;
import java.util.List;

/** Class implementing parsing for LocalVarSig, as specified in II.23.2.6 LocalVarSig. */
public class LocalVarSig {

  private static final String TYPE = "LocalVarSig";

  @CompilerDirectives.CompilationFinal(dimensions = 1)
  private final Type[] varTypes;

  public LocalVarSig(Type[] varTypes) {
    this.varTypes = varTypes;
  }

  public static LocalVarSig read(byte[] signature, CLIComponent component) {
    SignatureReader reader = new SignatureReader(signature);
    reader.assertUnsigned(7, TYPE);

    int count = reader.getUnsigned();
    Type[] varTypes = new Type[count];

    for (int i = 0; i < count; i++) {
      if (reader.peekUnsigned() == TypeSig.ELEMENT_TYPE_TYPEDBYREF) {
        varTypes[i] = component.getBuiltinTypes().getTypedReferenceType();
        continue;
      }

      boolean pinned = false;
      boolean byRef = false;

      List<CustomMod> mods = CustomMod.readAll(reader);

      if (reader.peekUnsigned() == TypeSig.ELEMENT_TYPE_PINNED) {
        pinned = true;
        reader.getUnsigned();
      }

      if (reader.peekUnsigned() == TypeSig.ELEMENT_TYPE_BYREF) {
        byRef = true;
        reader.getUnsigned();
      }

      Type type = TypeSig.read(reader, component);
      if (byRef) {
        type = new ByRefWrapped(type);
      }
      if (pinned) {
        type = new PinnedWrapped(type);
      }
      if (mods != null) {
        type = new CustomModWrapped(type, mods);
      }
      varTypes[i] = type;
    }

    return new LocalVarSig(varTypes);
  }

  public Type[] getVarTypes() {
    return varTypes;
  }
}
