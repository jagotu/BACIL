package com.vztekoverflow.cilostazol.runtime.symbols;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITableConstants;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeDefTableRow;
import com.vztekoverflow.cilostazol.runtime.other.ContextProviderImpl;
import com.vztekoverflow.cilostazol.runtime.symbols.utils.CLIFileUtils;

public final class ModuleSymbol extends Symbol {
  private final CLIFile definingFile;
  private final MethodSymbol[] methodCache;
  private final int[] methodToClassIndex;

  public ModuleSymbol(CLIFile definingFile) {
    super(ContextProviderImpl.getInstance());
    this.definingFile = definingFile;
    this.methodCache =
        new MethodSymbol
            [definingFile.getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_METHOD_DEF)];
    // Fill method to class indices
    this.methodToClassIndex =
        new int[definingFile.getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_METHOD_DEF) +1];
    for (CLITypeDefTableRow klass : definingFile.getTableHeads().getTypeDefTableHead()) {
      var methodRange = CLIFileUtils.getMethodRange(definingFile, klass);
      int startIdx = methodRange.getLeft();
      int endIdx = methodRange.getRight();
      while (startIdx < endIdx) {
        methodToClassIndex[startIdx] = klass.getPtr().getRowNo();
        startIdx++;
      }
    }
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

  /**
   * @return the method with the given index(Obtained from MethodDefPtr), or null if not found in
   *     this module.
   * @apiNote If found, the method is cached in the ModuleSymbol.
   */
  public MethodSymbol getLocalMethod(int index) {
    if (methodCache[index] == null) {
      var classRow =
          definingFile
              .getTableHeads()
              .getTypeDefTableHead()
              .skip(
                  new CLITablePtr(CLITableConstants.CLI_TABLE_TYPE_DEF, methodToClassIndex[index]));
      var nameAndNamespace = CLIFileUtils.getNameAndNamespace(definingFile, classRow);
      methodCache[index] =
          MethodSymbol.MethodSymbolFactory.create(
              definingFile
                  .getTableHeads()
                  .getMethodDefTableHead()
                  .skip(new CLITablePtr(CLITableConstants.CLI_TABLE_METHOD_DEF, index)),
              getContext()
                  .getType(
                      nameAndNamespace.getLeft(),
                      nameAndNamespace.getRight(),
                      definingFile.getAssemblyIdentity()));
    }

    return methodCache[index];
  }

  public static final class ModuleSymbolFactory {
    public static ModuleSymbol create(CLIFile file) {
      return new ModuleSymbol(file);
    }
  }
}
