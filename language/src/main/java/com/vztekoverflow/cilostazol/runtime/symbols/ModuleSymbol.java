package com.vztekoverflow.cilostazol.runtime.symbols;

import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITableConstants;
import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.runtime.other.ContextProviderImpl;

public class ModuleSymbol extends Symbol {
  private final CLIFile definingFile;
  private final NamedTypeSymbol[] containingTypesCache;

  public ModuleSymbol(CLIFile definingFile) {
    super(ContextProviderImpl.getInstance());
    this.definingFile = definingFile;
    this.containingTypesCache =
        new NamedTypeSymbol
            [definingFile.getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_TYPE_DEF)];
    InitializeTypes();
  }

  private void InitializeTypes() {
    for (int i = 0; i < containingTypesCache.length; i++) {
      containingTypesCache[i] =
          NamedTypeSymbol.NamedTypeSymbolFactory.create(
              definingFile.getTableHeads().getTypeDefTableHead().skip(i), this);
    }
  }

  public CLIFile getDefiningFile() {
    return definingFile;
  }

  /**
   * @return the type with the given name and namespace, or null if not found in this module.
   * @apiNote Uses internal cache
   */
  public NamedTypeSymbol getLocalType(String name, String namespace) {
    for (NamedTypeSymbol type : containingTypesCache) {
      if (type.getNamespace().equals(namespace) && type.getName().equals(name)) return type;
    }

    return null;
  }

  public static class ModuleSymbolFactory {
    public static ModuleSymbol create(CLIFile file) {
      return new ModuleSymbol(file);
    }
  }
}
