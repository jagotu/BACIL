package com.vztekoverflow.cilostazol.runtime.symbols;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.runtime.other.ContextProviderImpl;

public final class ModuleSymbol extends Symbol {
  private final CLIFile definingFile;

  public ModuleSymbol(CLIFile definingFile) {
    super(ContextProviderImpl.getInstance());
    this.definingFile = definingFile;
  }

  public CLIFile getDefiningFile() {
    return definingFile;
  }

  /**
   * @return the type with the given name and namespace, or null if not found in this module.
   * @apiNote If found, the type is cached in the context.
   */
  public NamedTypeSymbol getLocalType(String name, String namespace) {
    for (var row : definingFile.getTableHeads().getTypeDefTableHead()) {
      var rowName = row.getTypeNameHeapPtr().read(definingFile.getStringHeap());
      var rowNamespace = row.getTypeNamespaceHeapPtr().read(definingFile.getStringHeap());

      if (rowName.equals(name) && rowNamespace.equals(namespace)) {
        return NamedTypeSymbol.NamedTypeSymbolFactory.create(row, this);
      }
    }

    return null;
  }

  public static final class ModuleSymbolFactory {
    public static ModuleSymbol create(CLIFile file) {
      return new ModuleSymbol(file);
    }
  }
}
