package com.vztekoverflow.cilostazol.context;

import com.vztekoverflow.cilostazol.CILOSTAZOLLanguage;
import com.vztekoverflow.cilostazol.meta.Meta;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.GuestAllocator;

public abstract class ContextAccessImpl implements ContextAccess {
  private final CILOSTAZOLContext context;

  public ContextAccessImpl(CILOSTAZOLContext context) {
    this.context = context;
  }

  @Override
  public final Meta getMeta() {
    return getContext().getMeta();
  }

  @Override
  public final CILOSTAZOLContext getContext() {
    return context;
  }

  @Override
  public final CILOSTAZOLLanguage getLanguage() {
    return getContext().getLanguage();
  }

  @Override
  public final GuestAllocator getAllocator() {
    return getContext().getAllocator();
  }
}
