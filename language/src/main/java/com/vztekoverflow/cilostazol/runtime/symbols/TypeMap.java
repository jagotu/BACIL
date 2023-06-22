package com.vztekoverflow.cilostazol.runtime.symbols;

import com.vztekoverflow.cilostazol.exceptions.InvalidCLIException;

import java.util.Arrays;
import java.util.HashMap;

public class TypeMap {
  private final HashMap<TypeParameterSymbol, TypeSymbol> map;

  public TypeMap(TypeParameterSymbol[] from, TypeSymbol[] to) {
    this.map = new HashMap<>();
    for (int i = 0; i < from.length; i++) {
      map.put(from[i], to[i]);
    }
  }

  public TypeMap(TypeMap map, TypeParameterSymbol[] from, TypeSymbol[] to) {
    this.map = new HashMap<>();
    this.map.putAll(map.map);
    for (int i = 0; i < from.length; i++) {
      this.map.put(from[i], to[i]);
    }
  }

  private TypeSymbol substituteTypeParameter(TypeParameterSymbol symbol) {
    return map.getOrDefault(symbol, symbol);
  }

  private NamedTypeSymbol substituteNamedTypeSymbol(NamedTypeSymbol symbol) {
    var typeArgs = symbol.getTypeArguments();
    var replacedTypeArgs =
        Arrays.stream(typeArgs).map(x -> this.substitute(x)).toArray(TypeSymbol[]::new);
    return symbol.construct(replacedTypeArgs);
  }

  public TypeSymbol substitute(TypeSymbol symbol) {
    if (symbol instanceof TypeParameterSymbol) {
      return substituteTypeParameter((TypeParameterSymbol) symbol);
    } else if (symbol instanceof NamedTypeSymbol) {
      return substituteNamedTypeSymbol((NamedTypeSymbol) symbol);
    } else {
      throw new InvalidCLIException();
    }
  }
}
