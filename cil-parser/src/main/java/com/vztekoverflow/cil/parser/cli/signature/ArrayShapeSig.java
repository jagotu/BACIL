package com.vztekoverflow.cil.parser.cli.signature;

public record ArrayShapeSig(int rank, int[] lengths, int[] lowerBounds) {
  public static ArrayShapeSig read(SignatureReader reader) {
    final int rank = reader.getUnsigned();
    final int[] sizes = new int[reader.getUnsigned()];
    for (int i = 0; i < sizes.length; i++) {
      sizes[i] = reader.getUnsigned();
    }
    final int[] lowerBounds = new int[reader.getUnsigned()];
    for (int i = 0; i < lowerBounds.length; i++) {
      lowerBounds[i] = reader.getUnsigned();
    }
    return new ArrayShapeSig(rank, sizes, lowerBounds);
  }
}
