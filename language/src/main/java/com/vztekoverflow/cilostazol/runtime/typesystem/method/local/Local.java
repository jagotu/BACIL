package com.vztekoverflow.cilostazol.runtime.typesystem.method.local;

import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitution;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public class Local implements ILocal {
  protected boolean _pinned;
  private boolean _byRef;
  private IType _type;

  public Local(boolean _pinned, boolean _byRef, IType _type) {
    this._pinned = _pinned;
    this._byRef = _byRef;
    this._type = _type;
  }

  // region ILocal
  @Override
  public boolean isPinned() {
    return _pinned;
  }

  @Override
  public boolean isByRef() {
    return _byRef;
  }

  @Override
  public IType getType() {
    return _type;
  }

  @Override
  public ILocal substitute(ISubstitution<IType> substitution) {
    return new Local(_pinned, _byRef, _type.substitute(substitution));
  }

  @Override
  public ILocal getDefinition() {
    throw new NotImplementedException();
  }

  @Override
  public ILocal getConstructedFrom() {
    throw new NotImplementedException();
  }
  // endregion
}
