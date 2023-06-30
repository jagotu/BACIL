package com.vztekoverflow.cilostazol.runtime.symbols;

import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.cil.parser.CILParserException;
import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITableConstants;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.runtime.context.ContextProviderImpl;
import org.graalvm.polyglot.Source;

public final class AssemblySymbol extends Symbol {
  private final ModuleSymbol[] modules;
  private final CLIFile definingFile;

  private AssemblySymbol(ModuleSymbol[] modules, CLIFile definingFile) {
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
      final NamedTypeSymbol type = module.getLocalType(name, namespace);
      if (type != null) return type;
    }

    return null;
  }

  public AssemblyIdentity getIdentity() {
    return definingFile.getAssemblyIdentity();
  }

  public MethodSymbol getEntryPoint() {
    if (definingFile.getCliHeader().getEntryPointToken() == 0) {
      throw new RuntimeException(
          CILOSTAZOLBundle.message("cilostazol.exception.runtime.no.entrypoint"));
    }

    CLITablePtr entryPtr = CLITablePtr.fromToken(definingFile.getCliHeader().getEntryPointToken());
    return modules[0].getLocalMethod(entryPtr.getRowNo());
  }

  public static final class AssemblySymbolFactory {
    /**
     * Create an assembly from a DLL file. Is Pure.
     *
     * @param dllSource the DLL file
     * @return the assembly
     */
    public static AssemblySymbol create(Source dllSource) {
      CLIFile file = CLIFile.parse(dllSource.getName(), dllSource.getPath(), dllSource.getBytes());

      if (file.getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_MODULE) != 1)
        throw new CILParserException(CILOSTAZOLBundle.message("cilostazol.exception.module"));

      // TODO: multimodule assembly is not supported but is allowed so we can parse primitive types
      //      if (file.getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_MODULE_REF) > 0)
      //        throw new CILParserException(
      //            CILOSTAZOLBundle.message("cilostazol.exception.multimoduleAssembly"));

      return new AssemblySymbol(
          new ModuleSymbol[] {ModuleSymbol.ModuleSymbolFactory.create(file)}, file);
    }
  }
}
