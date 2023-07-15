package com.vztekoverflow.cilostazol.nodes;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.vztekoverflow.cilostazol.runtime.context.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.objectmodel.StaticObject;

import java.io.PrintStream;

public final class CallEntryPointCallTarget implements CallTarget {

  private final CallTarget inner;
  private final boolean shouldAddArgs;
  private final Object arg;

  public CallEntryPointCallTarget(CallTarget inner, boolean shouldAddArgs) {
    this.inner = inner;
    this.shouldAddArgs = shouldAddArgs;
    if (shouldAddArgs) {
      // TODO: this part is how we can implement Console.WriteLine, it is now here just for the test
      {
        TruffleLanguage.Env env = CILOSTAZOLContext.CONTEXT_REF.get(null).getEnv();

        try (var p = new PrintStream(env.out())) {
          p.println("TEST");
        }
      }
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
