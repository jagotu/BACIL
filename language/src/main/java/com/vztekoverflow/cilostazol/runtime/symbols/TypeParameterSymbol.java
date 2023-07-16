package com.vztekoverflow.cilostazol.runtime.symbols;

import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.generated.CLIGenericParamConstraintTableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.CLIGenericParamTableRow;
import com.vztekoverflow.cilostazol.nodes.CILOSTAZOLFrame;
import java.util.ArrayList;
import java.util.List;

public final class TypeParameterSymbol extends TypeSymbol {
  private TypeSymbol[] constraints;
  private final GenericParameterFlags flags;
  private final int ordinal;
  private final String name;

  private TypeParameterSymbol(
      ModuleSymbol definingModule,
      TypeSymbol[] constraints,
      GenericParameterFlags flags,
      int ordinal,
      String name) {
    super(definingModule, CILOSTAZOLFrame.StackType.Void);
    this.constraints = constraints;
    this.flags = flags;
    this.ordinal = ordinal;
    this.name = name;
  }

  public TypeSymbol[] getTypeConstrains() {
    return constraints;
  }

  public GenericParameterFlags getFlags() {
    return flags;
  }

  public String getName() {
    return name;
  }

  public int getOrdinal() {
    return ordinal;
  }

  public static class TypeParameterSymbolFactory {
    public static TypeParameterSymbol createWith(
        TypeParameterSymbol symbol, TypeSymbol[] constrains) {
      return new TypeParameterSymbol(
          symbol.definingModule, constrains, symbol.flags, symbol.ordinal, null);
    }

    public static TypeParameterSymbol create(
        CLIGenericParamTableRow row, TypeSymbol[] mvars, TypeSymbol[] vars, ModuleSymbol module) {
      final short flags = row.getFlags();
      return new TypeParameterSymbol(
          module,
          null,
          new GenericParameterFlags(flags),
          row.getNumber(),
          row.getNameHeapPtr().read(module.getDefiningFile().getStringHeap()));
    }

    public static TypeParameterSymbol[] create(
        int count, CLITablePtr ptr, TypeSymbol[] vars, ModuleSymbol module) {
      TypeParameterSymbol[] result = new TypeParameterSymbol[count];
      while (count-- > 0) {
        for (CLIGenericParamTableRow row :
            module.getDefiningFile().getTableHeads().getGenericParamTableHead()) {
          if (ptr.getTableId() == row.getOwnerTablePtr().getTableId()
              && ptr.getRowNo() == row.getOwnerTablePtr().getRowNo()) {
            result[row.getNumber()] = create(row, result, vars, module);
            result[row.getNumber()].constraints = getConstrains(row, result, vars, module);
          }
        }
      }

      return result;
    }

    public static TypeParameterSymbol[] create(
        CLITablePtr ptr, TypeSymbol[] vars, ModuleSymbol module) {
      var result = new ArrayList<TypeParameterSymbol>();
      for (CLIGenericParamTableRow row :
          module.getDefiningFile().getTableHeads().getGenericParamTableHead()) {
        if (ptr.getTableId() == row.getOwnerTablePtr().getTableId()
            && ptr.getRowNo() == row.getOwnerTablePtr().getRowNo()) {
          result.add(create(row, result.toArray(TypeParameterSymbol[]::new), vars, module));
        }
      }

      return result.toArray(TypeParameterSymbol[]::new);
    }

    private static TypeSymbol[] getConstrains(
        CLIGenericParamTableRow row, TypeSymbol[] mvars, TypeSymbol[] vars, ModuleSymbol module) {
      List<TypeSymbol> constrains = new ArrayList<>();
      for (CLIGenericParamConstraintTableRow r :
          module.getDefiningFile().getTableHeads().getGenericParamConstraintTableHead()) {
        if (row.getTableId() == r.getOwnerTablePtr().getTableId()
            && row.getRowNo() == r.getOwnerTablePtr().getRowNo()) {
          constrains.add(
              TypeSymbol.TypeSymbolFactory.create(r.getConstraintTablePtr(), mvars, vars, module));
        }
      }

      return constrains.toArray(new TypeSymbol[0]);
    }
  }

  public static class GenericParameterFlags {
    // region Masks
    private static final int F_VARIANCE_MASK = 0x0003;
    private static final int F_SPECIAL_CONSTRAINT_MASK = 0x001C;
    private final int _flags;
    // endregion

    public GenericParameterFlags(int flags) {
      _flags = flags;
    }

    public boolean hasFlag(GenericParameterFlags.Flag flag) {
      switch (flag) {
        case NONE:
          return ((_flags & F_VARIANCE_MASK) == flag.code);
        case COVARIANT:
        case CONTRAVARIANT:
          return !hasFlag(GenericParameterFlags.Flag.NONE)
              && ((_flags & F_VARIANCE_MASK) == flag.code);
        case REFERENCE_TYPE_CONSTRAINT:
        case NOT_NULLABLE_VALUE_TYPE_CONSTRAINT:
        case DEFAULT_CONSTRUCTOR_CONSTRAINT:
          return (_flags & F_SPECIAL_CONSTRAINT_MASK) == flag.code;
        default:
          return !hasFlag(GenericParameterFlags.Flag.NONE) && (_flags & flag.code) == flag.code;
      }
    }

    public enum Flag {
      NONE(0x0000),
      COVARIANT(0x0001),
      CONTRAVARIANT(0x0002),
      REFERENCE_TYPE_CONSTRAINT(0x0004),
      NOT_NULLABLE_VALUE_TYPE_CONSTRAINT(0x0008),
      DEFAULT_CONSTRUCTOR_CONSTRAINT(0x0010);

      public final int code;

      Flag(int code) {
        this.code = code;
      }
    }
  }
}
