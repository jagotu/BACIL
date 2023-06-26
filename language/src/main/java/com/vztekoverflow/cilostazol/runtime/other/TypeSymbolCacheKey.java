package com.vztekoverflow.cilostazol.runtime.other;

import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import java.util.Objects;

public record TypeSymbolCacheKey(String name, String namespace, AssemblyIdentity assemblyIdentity) {

  @Override
  public String toString() {
    return namespace + "." + name + " in " + assemblyIdentity;
  }

  @Override
  public int hashCode() {
    return name.hashCode() + namespace.hashCode() + assemblyIdentity.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TypeSymbolCacheKey that = (TypeSymbolCacheKey) o;
    return Objects.equals(name, that.name)
        && Objects.equals(namespace, that.namespace)
        && Objects.equals(assemblyIdentity, that.assemblyIdentity);
  }
}
