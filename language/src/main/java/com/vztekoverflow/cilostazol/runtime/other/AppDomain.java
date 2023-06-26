package com.vztekoverflow.cilostazol.runtime.other;

import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cilostazol.runtime.symbols.AssemblySymbol;
import java.util.ArrayList;

public class AppDomain {
  private final ArrayList<AssemblySymbol> loadedAssemblies;

  public AppDomain() {
    loadedAssemblies = new ArrayList<>();
  }

  public void loadAssembly(AssemblySymbol assembly) {
    loadedAssemblies.add(assembly);
  }

  public AssemblySymbol getAssembly(AssemblyIdentity identity) {
    AssemblySymbol result = null;

    for (int i = 0; i < loadedAssemblies.size() && result == null; i++) {
      if (loadedAssemblies.get(i).getIdentity().equals(identity)) result = loadedAssemblies.get(i);
    }

    return result;
  }
}
