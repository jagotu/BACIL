package com.vztekoverflow.bacil.runtime.bacil.internalcall;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.vztekoverflow.bacil.runtime.bacil.JavaMethod;
import com.vztekoverflow.bacil.runtime.types.ByRefWrapped;
import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.builtin.BuiltinTypes;

/** Implementation of System.ValueType.GetHashCode() */
public class ValueTypeGetHashCodeMethod extends JavaMethod {

  private final Type retType;

  @CompilerDirectives.CompilationFinal(dimensions = 1)
  private final Type[] argTypes;

  private final Type definingType;

  public ValueTypeGetHashCodeMethod(
      BuiltinTypes builtinTypes, TruffleLanguage<?> language, Type definingType) {
    super(language);
    retType = builtinTypes.getDoubleType();
    argTypes = new Type[] {new ByRefWrapped(definingType)};
    this.definingType = definingType;
  }

  @Override
  public Object execute(VirtualFrame frame) {
    return frame.getArguments()[0].hashCode();
  }

  @Override
  public Type getRetType() {
    return retType;
  }

  @Override
  public int getArgsCount() {
    return 1;
  }

  @Override
  public int getVarsCount() {
    return 0;
  }

  @Override
  public Type[] getLocationsTypes() {
    return argTypes;
  }

  @Override
  public Type getDefiningType() {
    return definingType;
  }
}
