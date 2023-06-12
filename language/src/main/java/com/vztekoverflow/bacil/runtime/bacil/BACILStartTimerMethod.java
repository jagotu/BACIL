package com.vztekoverflow.bacil.runtime.bacil;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.builtin.BuiltinTypes;

/**
 * Implementation of the BACILHelpers.BACILEnvironment.StartTimer() method.
 *
 * <p>Starts the system timer so that later BACILHelpers.BACILEnvironment.GetTicks() can be used to
 * get the elapsed time.
 */
public class BACILStartTimerMethod extends JavaMethod {

  private final Type retType;

  private final BACILEnvironmentType definingType;

  public BACILStartTimerMethod(
      BuiltinTypes builtinTypes, TruffleLanguage<?> language, BACILEnvironmentType definingType) {
    super(language);
    retType = builtinTypes.getVoidType();
    this.definingType = definingType;
  }

  @Override
  public Object execute(VirtualFrame frame) {
    definingType.timerStart = System.nanoTime();
    return null;
  }

  @Override
  public Type getRetType() {
    return retType;
  }

  @Override
  public int getArgsCount() {
    return 0;
  }

  @Override
  public int getVarsCount() {
    return 0;
  }

  @Override
  public Type[] getLocationsTypes() {
    return new Type[0];
  }

  @Override
  public Type getDefiningType() {
    return definingType;
  }

  @Override
  public String getName() {
    return "StartTimer";
  }
}
