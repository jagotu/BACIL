package com.vztekoverflow.cilostazol.runtime.other;

import com.vztekoverflow.cilostazol.CILOSTAZOLLanguage;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;

public interface ContextProvider {
  public CILOSTAZOLContext getContext();

  public CILOSTAZOLLanguage getLanguage();
}
