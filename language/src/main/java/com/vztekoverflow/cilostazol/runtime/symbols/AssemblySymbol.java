package com.vztekoverflow.cilostazol.runtime.symbols;

import com.vztekoverflow.cil.parser.CILParserException;
import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITableConstants;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.runtime.other.ContextProviderImpl;
import org.graalvm.polyglot.Source;

public class AssemblySymbol extends Symbol {
  private final ModuleSymbol[] modules;
  private final CLIFile definingFile;

  public AssemblySymbol(ModuleSymbol[] modules, CLIFile definingFile) {
    super(ContextProviderImpl.getInstance());
    this.modules = modules;
    this.definingFile = definingFile;
  }

  public CLIFile getDefiningFile() {
    return definingFile;
  }

  public ModuleSymbol[] getModules() {
    return modules;
  }

  public NamedTypeSymbol getLocalType(String name, String namespace) {
    for (ModuleSymbol module : getModules()) {
      final NamedTypeSymbol type = module.getLocalType(namespace, name);
      if (type != null) return type;
    }

    return null;
  }

  public AssemblyIdentity getIdentity() {
    return definingFile.getAssemblyIdentity();
  }

  public static class AssemblySymbolFactory {
    public static AssemblySymbol create(Source dllSource) {
      CLIFile file = CLIFile.parse(dllSource.getName(), dllSource.getPath(), dllSource.getBytes());

      if (file.getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_MODULE) != 1)
        throw new CILParserException(CILOSTAZOLBundle.message("cilostazol.exception.module"));

      if (file.getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_MODULE_REF) > 0)
        throw new CILParserException(
            CILOSTAZOLBundle.message("cilostazol.exception.multimoduleAssembly"));

      return new AssemblySymbol(
          new ModuleSymbol[] {ModuleSymbol.ModuleSymbolFactory.create(file)}, file);
    }
  }
}
