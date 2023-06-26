package com.vztekoverflow.cilostazol.runtime.symbols;

import java.util.Arrays;

public class SubstitutedMethodSymbol extends MethodSymbol {
  private final MethodSymbol definition;
  private final MethodSymbol constructedFrom;
  protected final TypeMap map;

  protected SubstitutedMethodSymbol(
      MethodSymbol definition,
      MethodSymbol constructedFrom,
      NamedTypeSymbol containingType,
      TypeMap map) {
    super(
        constructedFrom.name,
        constructedFrom.module,
        containingType,
        constructedFrom.methodDefFlags,
        constructedFrom.methodFlags,
        constructedFrom.methodImplFlags,
        createTypeParameters(constructedFrom.typeParameters, map),
        createParams(constructedFrom.parameters, map),
        createLocals(constructedFrom.locals, map),
        createReturn(constructedFrom.retType, map),
        createHandlers(constructedFrom.exceptionHandlers, map),
        constructedFrom.cil,
        constructedFrom.maxStack,
        constructedFrom.methodHeaderFlags);
    this.definition = definition;
    this.constructedFrom = constructedFrom;
    this.map = map;
  }

  private static TypeParameterSymbol[] createTypeParameters(
      TypeParameterSymbol[] symbols, TypeMap map) {
    return Arrays.stream(symbols)
        .map(
            x ->
                TypeParameterSymbol.TypeParameterSymbolFactory.createWith(
                    x,
                    Arrays.stream(x.getTypeConstrains())
                        .map(y -> map.substitute(y))
                        .toArray(TypeSymbol[]::new)))
        .toArray(TypeParameterSymbol[]::new);
  }

  private static ParameterSymbol[] createParams(ParameterSymbol[] symbols, TypeMap map) {
    return Arrays.stream(symbols)
        .map(x -> ParameterSymbol.ParameterSymbolFactory.createWith(x, map.substitute(x.getType())))
        .toArray(ParameterSymbol[]::new);
  }

  private static LocalSymbol[] createLocals(LocalSymbol[] symbols, TypeMap map) {
    return Arrays.stream(symbols)
        .map(x -> LocalSymbol.LocalSymbolFactory.createWith(x, map.substitute(x.getType())))
        .toArray(LocalSymbol[]::new);
  }

  private static ReturnSymbol createReturn(ReturnSymbol symbol, TypeMap map) {
    return ReturnSymbol.ReturnSymbolFactory.createWith(symbol, map.substitute(symbol.getType()));
  }

  private static ExceptionHandlerSymbol[] createHandlers(
      ExceptionHandlerSymbol[] symbols, TypeMap map) {
    return Arrays.stream(symbols)
        .map(
            x ->
                ExceptionHandlerSymbol.ExceptionHandlerSymbolFactory.createWith(
                    x, map.substitute(x.getHandlerException())))
        .toArray(ExceptionHandlerSymbol[]::new);
  }

  public static final class SubstitutedMethodSymbolFactory {
    public static SubstitutedMethodSymbol create(
        MethodSymbol definition, MethodSymbol constructedFrom, NamedTypeSymbol containingType) {
      return new SubstitutedMethodSymbol(
          definition,
          constructedFrom,
          containingType,
          new TypeMap(
              constructedFrom.getDefiningType().getTypeParameters(),
              containingType.getTypeArguments()));
    }
  }
}
