package com.vztekoverflow.cilostazol.runtime.symbols;

public final class ConstructedMethodSymbol extends SubstitutedMethodSymbol {
  private final TypeSymbol[] typeArguments;

  private ConstructedMethodSymbol(
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

  public static final class ConstructedMethodSymbolFactory {
    public static ConstructedMethodSymbol create(
        MethodSymbol definition, MethodSymbol constructedFrom, TypeSymbol[] typeArguments) {
      return new ConstructedMethodSymbol(definition, constructedFrom, typeArguments);
    }
  }
}
