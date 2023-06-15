package com.vztekoverflow.cilostazol.runtime.typesystem.method;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.CLIComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;

public abstract class CLIMethod implements IMethod {
  public abstract CLIComponent getCLIComponent();

  public CLIFile getFile() {
    return getCLIComponent().getDefiningFile();
  }

  @Override
  public IComponent getDefiningComponent() {
    return getCLIComponent();
  }
}
