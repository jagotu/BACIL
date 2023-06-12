package com.vztekoverflow.bacil.runtime.types.builtin;

import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITypeDefTableRow;
import com.vztekoverflow.bacil.runtime.types.CLIType;

/** Implementation of the System.String type. */
public class SystemStringType extends CLIType {
  public SystemStringType(CLITypeDefTableRow type, CLIComponent component) {
    super(type, component);
  }

  // String is a generic reference type and doesn't need any special code (yet).

}
