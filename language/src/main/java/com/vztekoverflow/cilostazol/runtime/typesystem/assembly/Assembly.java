package com.vztekoverflow.cilostazol.runtime.typesystem.assembly;

import com.vztekoverflow.cil.parser.CILParserException;
import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITableConstants;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.typesystem.appdomain.IAppDomain;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.CLIComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;
import org.graalvm.polyglot.Source;

public class Assembly implements IAssembly {
  private final IComponent[] components;
  private final CLIFile file;
  private IAppDomain appDomain;

  // region IAssembly
  @Override
  public CLIFile getDefiningFile() {
    return file;
  }

  public IComponent[] getComponents() {
    return components;
  }

  @Override
  public IType getLocalType(String namespace, String name) {
    IType result = null;
    for (int i = 0; i < components.length && result == null; i++) {
      result = components[i].getLocalType(namespace, name);
    }

    return result;
  }

  @Override
  public AssemblyIdentity getIdentity() {
    return file.getAssemblyIdentity();
  }

  @Override
  public void setAppDomain(IAppDomain appdomain) {
    appDomain = appdomain;
  }

  @Override
  public IAppDomain getAppDomain() {
    return appDomain;
  }

  @Override
  public CILOSTAZOLContext getContext() {
    if (appDomain == null)
      throw new CILParserException(CILOSTAZOLBundle.message("cilostazol.exception.noAppDomain"));

    return appDomain.getContext();
  }

  // endregion

  private Assembly(CLIFile file, IComponent[] components) {
    this.file = file;
    this.components = components;
    appDomain = null;
  }

  public static IAssembly parse(IAppDomain domain, Source dllSource) {
    CLIFile file = CLIFile.parse(dllSource.getName(), dllSource.getPath(), dllSource.getBytes());

    if (file.getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_MODULE) != 1)
      throw new CILParserException(CILOSTAZOLBundle.message("cilostazol.exception.module"));

    if (file.getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_MODULE_REF) > 0)
      throw new CILParserException(
          CILOSTAZOLBundle.message("cilostazol.exception.multimoduleAssembly"));

    Assembly assembly = new Assembly(file, new IComponent[1]);
    domain.loadAssembly(assembly);
    assembly.components[0] = CLIComponent.parse(file, assembly);

    return assembly;
  }
}
