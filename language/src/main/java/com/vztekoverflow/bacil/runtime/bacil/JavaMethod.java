package com.vztekoverflow.bacil.runtime.bacil;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.nodes.RootNode;
import com.vztekoverflow.bacil.parser.signatures.MethodDefSig;
import com.vztekoverflow.bacil.runtime.BACILMethod;

/** Class for implementing BACIL methods in Java. */
public abstract class JavaMethod extends RootNode implements BACILMethod {

  private final CallTarget callTarget = getCallTarget();

  protected JavaMethod(TruffleLanguage<?> language) {
    super(language);
  }

  protected JavaMethod(TruffleLanguage<?> language, FrameDescriptor frameDescriptor) {
    super(language, frameDescriptor);
  }

  @Override
  public CallTarget getMethodCallTarget() {
    return callTarget;
  }

  @Override
  public boolean isVirtual() {
    return false;
  }

  @Override
  public MethodDefSig getSignature() {
    return null;
  }
}
