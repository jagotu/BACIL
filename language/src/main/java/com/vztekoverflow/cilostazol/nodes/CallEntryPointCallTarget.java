package com.vztekoverflow.cilostazol.nodes;

import com.oracle.truffle.api.CallTarget;
import com.vztekoverflow.cilostazol.runtime.objectmodel.StaticObject;

public final class CallEntryPointCallTarget implements CallTarget {

  private final CallTarget inner;
  private final boolean shouldAddArgs;
  private final Object arg;

  public CallEntryPointCallTarget(CallTarget inner, boolean shouldAddArgs) {
    this.inner = inner;
    this.shouldAddArgs = shouldAddArgs;
    if (shouldAddArgs) {
      arg = StaticObject.NULL; // TODO: Make as Static object array
    } else {
      arg = null;
    }
  }

  @Override
  public Object call(Object... arguments) {
    assert arguments.length == 0;
    Object result;
    if (shouldAddArgs) {
      result = inner.call(arg);
    } else {
      result = inner.call();
    }

    if (result == null) return 0;

    return result;
  }
}
