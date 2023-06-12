package com.vztekoverflow.bacil.runtime.types;

import com.vztekoverflow.bacil.parser.signatures.MethodDefSig;
import com.vztekoverflow.bacil.runtime.BACILMethod;
import java.util.List;

/**
 * Represents a type with a byref constraint, as specified in I.12.4.1.5.2 By-reference parameters
 */
public class ByRefWrapped extends Type {

  private final Type inner;

  public ByRefWrapped(Type inner) {
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
    return true;
  }

  @Override
  public boolean isPinned() {
    return inner.isPinned();
  }

  @Override
  public List<CustomMod> getMods() {
    return inner.getMods();
  }
}
