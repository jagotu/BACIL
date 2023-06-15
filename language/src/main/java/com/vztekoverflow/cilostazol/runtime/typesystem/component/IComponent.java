package com.vztekoverflow.cilostazol.runtime.typesystem.component;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITableHeads;
import com.vztekoverflow.cilostazol.context.ContextAccess;
import com.vztekoverflow.cilostazol.runtime.typesystem.assembly.IAssembly;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public interface IComponent extends ContextAccess {
  // TODO: CLIFile in IComponent ... get rid of it, or abandon IComponent completely?
  CLIFile getDefiningFile();

  IAssembly getDefiningAssembly();

  IType getLocalType(String namespace, String name);

  IType getLocalType(CLITablePtr typeIndex);

  // TODO: CLITableHeads in IComponent ... get rid of it, or abandon IComponent completely?
  CLITableHeads getTableHeads();

  // Note: CIL modules also have identity which can be used in TypeRef table.
  // However, for simplicity, we will always ask assembly to find desired type(typename, namespace).
  // Assembly will probe all of its module to find the name.
}
