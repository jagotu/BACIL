package com.vztekoverflow.cilostazol.runtime.symbols;

public class ConstructedNamedTypeSymbol extends NamedTypeSymbol {
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
        constructedFrom.typeParameters,
        constructedFrom.definingRow);
    this.constructedFrom = constructedFrom;
    this.originalDefinition = originalDefinition;
    this.typeArguments = typeArguments;
  }

  @Override
  public ConstructedNamedTypeSymbol Construct(TypeSymbol[] typeArguments) {
    return new ConstructedNamedTypeSymbol(originalDefinition, this, typeArguments);
  }
}
