package com.vztekoverflow.bacil.runtime.types.builtin;

import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITypeDefTableRow;
import com.vztekoverflow.bacil.runtime.types.CLIType;

/** Implementation of the System.Object type. */
public class SystemObjectType extends CLIType {
  public SystemObjectType(CLITypeDefTableRow type, CLIComponent component) {
    super(type, component);
  }

  // Object is a generic reference type and doesn't need any special code (yet).
}
