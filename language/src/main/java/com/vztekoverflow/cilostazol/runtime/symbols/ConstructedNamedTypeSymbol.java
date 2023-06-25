package com.vztekoverflow.cilostazol.runtime.symbols;

import java.util.Arrays;

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
        constructedFrom.definingRow,
        new TypeMap(constructedFrom.typeParameters, typeArguments));
    this.constructedFrom = constructedFrom;
    this.originalDefinition = originalDefinition;
    this.typeArguments = typeArguments;
  }

  @Override
  public ConstructedNamedTypeSymbol construct(TypeSymbol[] typeArguments) {
    return new ConstructedNamedTypeSymbol(originalDefinition, this, typeArguments);
  }

  // region Getters
  @Override
  public TypeSymbol getDirectBaseClass() {
    if (lazyDirectBaseClass == null) {
      lazyDirectBaseClass = map.substitute(constructedFrom.getDirectBaseClass());
    }

    return lazyDirectBaseClass;
  }

  @Override
  public TypeSymbol[] getInterfaces() {
    if (lazyInterfaces == null) {
      lazyInterfaces =
          Arrays.stream(constructedFrom.getInterfaces())
              .map(x -> map.substitute(x))
              .toArray(TypeSymbol[]::new);
    }

    return lazyInterfaces;
  }

  @Override
  public MethodSymbol[] getMethods() {
    if (lazyMethods == null) {
      lazyMethods =
          Arrays.stream(constructedFrom.getMethods())
              .map(x -> new SubstitutedMethodSymbol(x.getDefinition(), x, this))
              .toArray(MethodSymbol[]::new);
    }

    return lazyMethods;
  }

  @Override
  public MethodSymbol[] getVMT() {
    if (lazyVMethodTable == null) {
      lazyVMethodTable =
          Arrays.stream(constructedFrom.getMethods())
              .map(x -> new SubstitutedMethodSymbol(x.getDefinition(), x, this))
              .toArray(MethodSymbol[]::new);
    }

    return lazyVMethodTable;
  }

  @Override
  public FieldSymbol[] getFields() {
    if (lazyFields == null) {
      // TODO: substituted field symbols
    }

    return lazyFields;
  }
  // endregion
}
