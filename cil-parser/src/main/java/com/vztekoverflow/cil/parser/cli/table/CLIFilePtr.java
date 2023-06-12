package com.vztekoverflow.cil.parser.cli.table;

import com.vztekoverflow.cil.parser.cli.CLIFile;

/**
 * A class combining a table pointer with the component it belongs to. Used for implementing the
 * ldtoken instruction.
 */
public class CLIFilePtr {
  private final CLITablePtr ptr;
  private final CLIFile cliFile;

  public CLIFilePtr(CLITablePtr ptr, CLIFile component) {
    this.ptr = ptr;
    this.cliFile = component;
  }

  public CLITablePtr getPtr() {
    return ptr;
  }

  public CLIFile getCLIFile() {
    return cliFile;
  }
}
