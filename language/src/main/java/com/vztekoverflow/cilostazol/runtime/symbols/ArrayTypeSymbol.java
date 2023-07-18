package com.vztekoverflow.cilostazol.runtime.symbols;

import com.vztekoverflow.cil.parser.cli.signature.ArrayShapeSig;
import com.vztekoverflow.cilostazol.nodes.CILOSTAZOLFrame;
import com.vztekoverflow.cilostazol.runtime.objectmodel.SystemTypes;

public final class ArrayTypeSymbol extends TypeSymbol {
  private final TypeSymbol elementType;
  private final int rank;
  private final int[] lengths;
  private final int[] lowerBounds;

  private ArrayTypeSymbol(
      TypeSymbol elementType,
      int rank,
      int[] lengths,
      int[] lowerBounds,
      ModuleSymbol definingModule) {
    super(definingModule, CILOSTAZOLFrame.StackType.Object, SystemTypes.Object);
    this.elementType = elementType;
    this.rank = rank;
    this.lengths = lengths;
    this.lowerBounds = lowerBounds;
  }

  public TypeSymbol getElementType() {
    return elementType;
  }

  public int getRank() {
    return rank;
  }

  public int[] getLengths() {
    return lengths;
  }

  public int[] getLowerBounds() {
    return lowerBounds;
  }

  public static class ArrayTypeSymbolFactory {
    public static ArrayTypeSymbol create(
        TypeSymbol elementType, ArrayShapeSig arrayShapeSig, ModuleSymbol definingModule) {
      return new ArrayTypeSymbol(
          elementType,
          arrayShapeSig.rank(),
          arrayShapeSig.lengths(),
          arrayShapeSig.lowerBounds(),
          definingModule);
    }

    public static ArrayTypeSymbol create(TypeSymbol elementType, ModuleSymbol definingModule) {
      return new ArrayTypeSymbol(elementType, 1, new int[0], new int[0], definingModule);
    }
  }
}
