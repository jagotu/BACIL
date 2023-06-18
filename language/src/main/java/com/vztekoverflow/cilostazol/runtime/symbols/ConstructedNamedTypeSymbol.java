package com.vztekoverflow.cilostazol.runtime.symbols;

public abstract class ConstructedNamedTypeSymbol extends NamedTypeSymbol {
  private final NamedTypeSymbol constructedFrom;
  private final NamedTypeSymbol originalDefinition;
  private final TypeSymbol[] typeArguments;

  protected ConstructedNamedTypeSymbol(
      NamedTypeSymbol constructedFrom,
      NamedTypeSymbol originalDefinition,
      TypeSymbol[] typeArguments) {
    super(
        constructedFrom.definingModule,
        constructedFrom.flags,
        constructedFrom.name,
        constructedFrom.namespace,
        constructedFrom.typeParameters);
    this.constructedFrom = constructedFrom;
    this.originalDefinition = originalDefinition;
    this.typeArguments = typeArguments;
  }
}
