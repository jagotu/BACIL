package com.vztekoverflow.cilostazol.runtime.typesystem.appdomain;

import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cilostazol.context.ContextAccessImpl;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.typesystem.assembly.IAssembly;
import java.util.ArrayList;

public class AppDomain extends ContextAccessImpl implements IAppDomain {
  private final ArrayList<IAssembly> _assemblies;

  public AppDomain(CILOSTAZOLContext ctx) {
    super(ctx);
    _assemblies = new ArrayList<>();
  }

  // region IAppDomain
  @Override
  public IAssembly[] getAssemblies() {
    return _assemblies.toArray(new IAssembly[0]);
  }

  @Override
  public IAssembly getAssembly(AssemblyIdentity identity) {
    IAssembly result = null;

    for (int i = 0; i < _assemblies.size() && result == null; i++) {
      if (_assemblies.get(i).getIdentity().resolvesRef(identity)) result = _assemblies.get(i);
    }

    return result;
  }

  @Override
  public void loadAssembly(IAssembly assembly) {
    assembly.setAppDomain(this);
    _assemblies.add(assembly);
  }
  // endregion
}
