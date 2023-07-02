package com.vztekoverflow.cilostazol.runtime.context;

import com.vztekoverflow.cilostazol.CILOSTAZOLLanguage;

public interface ContextProvider {
  public CILOSTAZOLContext getContext();

  public CILOSTAZOLLanguage getLanguage();
}
