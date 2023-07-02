package com.vztekoverflow.cilostazol.runtime.symbols;

import com.vztekoverflow.cil.parser.cli.signature.FieldSig;
import com.vztekoverflow.cil.parser.cli.signature.SignatureReader;
import com.vztekoverflow.cil.parser.cli.table.generated.CLIFieldTableRow;
import com.vztekoverflow.cilostazol.meta.SystemTypes;
import com.vztekoverflow.cilostazol.runtime.context.ContextProviderImpl;
import com.vztekoverflow.cilostazol.runtime.symbols.utils.FieldSymbolVisibility;

public final class FieldSymbol extends Symbol {

  private final String name;
  private final TypeSymbol type;
  private final short flags;
  private final short visibilityFlags;
  private final TypeSymbol declaringType;

  private FieldSymbol(String name, TypeSymbol type, short flags, short visibilityFlags) {
    super(ContextProviderImpl.getInstance());
    this.name = name;
    this.type = type;
    this.flags = flags;
    this.visibilityFlags = visibilityFlags;
    // TODO:
    this.declaringType = null;
  }

  public String getName() {
    return name;
  }

  public TypeSymbol getType() {
    return type;
  }

  public boolean isStatic() {
    return (flags & 0x0010) != 0;
  }

  public boolean isInitOnly() {
    return (flags & 0x0020) != 0;
  }

  public boolean isLiteral() {
    return (flags & 0x0040) != 0;
  }

  public boolean isNotSerialized() {
    return (flags & 0x0080) != 0;
  }

  public boolean isSpecialName() {
    return (flags & 0x0200) != 0;
  }

  public FieldSymbolVisibility getVisibility() {
    return FieldSymbolVisibility.fromFlags(visibilityFlags);
  }

  public SystemTypes getKind() {
    return type.getKind();
  }

  public TypeSymbol getDeclaringType() {
    return declaringType;
  }

  public static class FieldSymbolFactory {
    public static FieldSymbol create(
        CLIFieldTableRow row, TypeSymbol[] mvars, TypeSymbol[] vars, ModuleSymbol module) {
      final String name = row.getNameHeapPtr().read(module.getDefiningFile().getStringHeap());
      final var signature = row.getSignatureHeapPtr().read(module.getDefiningFile().getBlobHeap());

      final FieldSig fieldSig = FieldSig.parse(new SignatureReader(signature));
      final TypeSymbol type =
          TypeSymbol.TypeSymbolFactory.create(fieldSig.getType(), mvars, vars, module);
      short flags = row.getFlags();
      short visibilityFlags = (short) (row.getFlags() & 0x0007);

      return new FieldSymbol(name, type, flags, visibilityFlags);
    }

    public static FieldSymbol createWith(FieldSymbol symbol, TypeSymbol type) {
      return new FieldSymbol(symbol.name, type, symbol.flags, symbol.visibilityFlags);
    }
  }
}
