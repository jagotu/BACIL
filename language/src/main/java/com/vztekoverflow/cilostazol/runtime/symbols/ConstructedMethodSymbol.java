package com.vztekoverflow.cilostazol.runtime.symbols;

public class ConstructedMethodSymbol extends MethodSymbol {
  private final TypeSymbol[] typeArguments;
  private final MethodSymbol definition;
  private final MethodSymbol constructedFrom;

  public ConstructedMethodSymbol(
      MethodSymbol definition, MethodSymbol constructedFrom, TypeSymbol[] typeArguments) {
    super(
        constructedFrom.name,
        constructedFrom.module,
        constructedFrom.definingType,
        constructedFrom.methodDefFlags,
        constructedFrom.methodFlags,
        constructedFrom.methodImplFlags,
        constructedFrom.typeParameters,
        constructedFrom.parameters,
        constructedFrom.locals,
        constructedFrom.retType,
        constructedFrom.exceptionHandlers,
        constructedFrom.cil,
        constructedFrom.maxStack,
        constructedFrom.methodHeaderFlags);
    // TODO: make substitutions
    this.definition = definition;
    this.constructedFrom = constructedFrom;
    this.typeArguments = typeArguments;
  }

  @Override
  public ConstructedMethodSymbol Construct(TypeSymbol[] typeArguments) {
    return new ConstructedMethodSymbol(definition, this, typeArguments);
  }
}
