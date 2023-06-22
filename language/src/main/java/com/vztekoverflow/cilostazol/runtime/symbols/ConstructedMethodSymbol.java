package com.vztekoverflow.cilostazol.runtime.symbols;

public class ConstructedMethodSymbol extends SubstitutedMethodSymbol {
  private final TypeSymbol[] typeArguments;

  public ConstructedMethodSymbol(
      MethodSymbol definition, MethodSymbol constructedFrom, TypeSymbol[] typeArguments) {
    super(
        definition,
        constructedFrom,
        constructedFrom.getDefiningType(),
        new TypeMap(
            constructedFrom.getDefiningType().getTypeMap(),
            constructedFrom.getTypeParameters(),
            typeArguments));
    this.typeArguments = typeArguments;
  }

  @Override
  public ConstructedMethodSymbol construct(TypeSymbol[] typeArguments) {
    return new ConstructedMethodSymbol(getDefinition(), this, typeArguments);
  }
}
