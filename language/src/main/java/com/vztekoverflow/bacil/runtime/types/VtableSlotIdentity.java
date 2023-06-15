package com.vztekoverflow.bacil.runtime.types;

import com.vztekoverflow.bacil.parser.signatures.MethodDefSig;
import com.vztekoverflow.bacil.runtime.BACILMethod;

public class VtableSlotIdentity {
  private final String name;
  private final MethodDefSig signature;

  public VtableSlotIdentity(String name, MethodDefSig signature) {
    this.name = name;
    this.signature = signature;
  }

  public String getName() {
    return name;
  }

  public MethodDefSig getSignature() {
    return signature;
  }

  public boolean resolves(BACILMethod method) {
    return name.equals(method.getName()) && signature.compatibleWith(method.getSignature());
  }
}
