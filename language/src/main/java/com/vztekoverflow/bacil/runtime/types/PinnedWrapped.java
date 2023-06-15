package com.vztekoverflow.bacil.runtime.types;

import com.vztekoverflow.bacil.parser.signatures.MethodDefSig;
import com.vztekoverflow.bacil.runtime.BACILMethod;
import java.util.List;

/** Represents a type with a pinned constraint, as specified in II.7.1.2 pinned. */
public class PinnedWrapped extends Type {
  private final Type inner;

  public PinnedWrapped(Type inner) {
    this.inner = inner;
  }

  public Type getInner() {
    return inner;
  }

  @Override
  public Type getDirectBaseClass() {
    return inner.getDirectBaseClass();
  }

  @Override
  public BACILMethod getMemberMethod(String name, MethodDefSig signature) {
    return inner.getMemberMethod(name, signature);
  }

  @Override
  public boolean isByRef() {
    return inner.isByRef();
  }

  @Override
  public boolean isPinned() {
    return true;
  }

  @Override
  public List<CustomMod> getMods() {
    return inner.getMods();
  }
}
