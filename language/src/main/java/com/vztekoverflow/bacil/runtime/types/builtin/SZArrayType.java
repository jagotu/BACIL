package com.vztekoverflow.bacil.runtime.types.builtin;

import com.vztekoverflow.bacil.parser.cil.CILMethod;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.signatures.MethodDefSig;
import com.vztekoverflow.bacil.parser.signatures.SignatureReader;
import com.vztekoverflow.bacil.parser.signatures.TypeSig;
import com.vztekoverflow.bacil.runtime.types.CustomMod;
import com.vztekoverflow.bacil.runtime.types.Type;
import java.util.List;

/** Represents a SZArray with a specific element type. */
public class SZArrayType extends Type {

  public static final String TYPE = "SZArrayType";
  private final Type inner;

  public SZArrayType(Type inner) {
    this.inner = inner;
  }

  public static SZArrayType read(SignatureReader reader, CLIComponent component) {
    List<CustomMod> mods = CustomMod.readAll(reader);

    Type inner = TypeSig.read(reader, component);

    return new SZArrayType(inner);
  }

  public Type getInner() {
    return inner;
  }

  @Override
  public Type getDirectBaseClass() {
    // TODO return system.array
    return null;
  }

  @Override
  public CILMethod getMemberMethod(String name, MethodDefSig signature) {
    return null;
  }

  @Override
  public boolean isByRef() {
    return false;
  }

  @Override
  public boolean isPinned() {
    return false;
  }

  @Override
  public List<CustomMod> getMods() {
    return null;
  }
}
