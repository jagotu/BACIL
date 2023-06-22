package com.vztekoverflow.cilostazol.runtime.symbols;

import java.util.Arrays;

public class SubstitutedMethodSymbol extends MethodSymbol {
  private final MethodSymbol definition;
  private final MethodSymbol constructedFrom;
  protected final TypeMap map;

  public SubstitutedMethodSymbol(
      MethodSymbol definition, MethodSymbol constructedFrom, NamedTypeSymbol containingType) {
    this(
        definition,
        constructedFrom,
        containingType,
        new TypeMap(
            constructedFrom.getDefiningType().getTypeParameters(),
            containingType.getTypeArguments()));
  }

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
        constructedFrom.typeParameters,
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

  private static ParameterSymbol[] createParams(ParameterSymbol[] symbols, TypeMap map) {
    return Arrays.stream(symbols)
        .map(
            x ->
                new ParameterSymbol(
                    x.isByRef(),
                    map.substitute(x.getType()),
                    x.getName(),
                    x.getIndex(),
                    x.getFlags()))
        .toArray(ParameterSymbol[]::new);
  }

  private static LocalSymbol[] createLocals(LocalSymbol[] symbols, TypeMap map) {
    return Arrays.stream(symbols)
        .map(x -> new LocalSymbol(x.isPinned(), x.isByRef(), map.substitute(x.getType())))
        .toArray(LocalSymbol[]::new);
  }

  private static ReturnSymbol createReturn(ReturnSymbol symbol, TypeMap map) {
    return new ReturnSymbol(symbol.isByRef(), map.substitute(symbol.getType()));
  }

  private static ExceptionHandlerSymbol[] createHandlers(
      ExceptionHandlerSymbol[] symbols, TypeMap map) {
    return Arrays.stream(symbols)
        .map(
            x ->
                new ExceptionHandlerSymbol(
                    x.getTryOffset(),
                    x.getTryLength(),
                    x.getHandlerOffset(),
                    x.getHandlerLength(),
                    map.substitute(x.getHandlerException()),
                    x.getFlags()))
        .toArray(ExceptionHandlerSymbol[]::new);
  }
}
