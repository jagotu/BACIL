package com.vztekoverflow.cilostazol.runtime.symbols;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.cil.parser.cli.signature.TypeSig;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.CLIComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public class NamedTypeSymbol extends TypeSymbol {
  protected final int flags;
  protected final String name;
  protected final String namespace;

  @CompilerDirectives.CompilationFinal protected TypeSymbol lazyDirectBaseClass;
  @CompilerDirectives.CompilationFinal protected TypeSymbol[] lazyInterfaces;
  @CompilerDirectives.CompilationFinal protected MethodSymbol[] lazyMethods;
  @CompilerDirectives.CompilationFinal protected MethodSymbol[] lazyVMethodTable;
  @CompilerDirectives.CompilationFinal protected FieldSymbol[] lazyFields;
  protected final TypeParameterSymbol[] typeParameters;

  protected NamedTypeSymbol(
      ModuleSymbol definingModule,
      int flags,
      String name,
      String namespace,
      TypeParameterSymbol[] typeParameters) {
    super(definingModule);
    this.flags = flags;
    this.name = name;
    this.namespace = namespace;
    this.typeParameters = typeParameters;
  }

  public TypeSymbol[] getTypeArguments() {
    return new TypeSymbol[0];
  }

  public static class NamedTypeSymbolFactory
  {
    public static NamedTypeSymbol create(CLITablePtr ptr, TypeSymbol[] mvars, TypeSymbol[] vars, ModuleSymbol module)
    {
      throw new NotImplementedException();
    }

    public static NamedTypeSymbol create(TypeSig sig, TypeSymbol[] mvars, TypeSymbol[] vars, ModuleSymbol module)
    {
      throw new NotImplementedException();
    }
  }
}
