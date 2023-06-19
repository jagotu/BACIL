package com.vztekoverflow.cilostazol.runtime.symbols;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.cil.parser.cli.signature.TypeSig;
import com.vztekoverflow.cil.parser.cli.signature.TypeSpecSig;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITableConstants;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeDefTableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeRefTableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeSpecTableRow;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;
import com.vztekoverflow.cilostazol.runtime.typesystem.TypeSystemException;
import java.util.ArrayList;

public class NamedTypeSymbol extends TypeSymbol {
  protected final int flags;
  protected final String name;
  protected final String namespace;

  @CompilerDirectives.CompilationFinal protected TypeSymbol lazyDirectBaseClass;
  @CompilerDirectives.CompilationFinal protected TypeSymbol[] lazyInterfaces;
  @CompilerDirectives.CompilationFinal protected MethodSymbol[] lazyMethods;
  @CompilerDirectives.CompilationFinal protected MethodSymbol[] lazyVMethodTable;
  @CompilerDirectives.CompilationFinal protected FieldSymbol[] lazyFields;
  protected final TypeParameterSymbol[] typeParameters;

  protected final CLITablePtr definingRow;

  protected NamedTypeSymbol(
      ModuleSymbol definingModule,
      int flags,
      String name,
      String namespace,
      TypeParameterSymbol[] typeParameters,
      CLITablePtr definingRow) {
    super(definingModule);
    this.flags = flags;
    this.name = name;
    this.namespace = namespace;
    this.typeParameters = typeParameters;
    this.definingRow = definingRow;
  }

  // region Getters
  public String getName() {
    return name;
  }

  public String getNamespace() {
    return namespace;
  }

  public TypeSymbol getDirectBaseClass() {
    throw new NotImplementedException();
  }

  public TypeSymbol[] getInterfaces() {
    throw new NotImplementedException();
  }

  public MethodSymbol[] getMethods() {
    if (lazyMethods == null) {
      // TODO: create on demand
      lazyMethods =
          switch (definingRow.getTableId()) {
            case CLITableConstants.CLI_TABLE_TYPE_DEF -> LazyFactory.createMethods(
                this,
                getDefiningModule()
                    .getDefiningFile()
                    .getTableHeads()
                    .getTypeDefTableHead()
                    .skip(definingRow));
            case CLITableConstants.CLI_TABLE_TYPE_REF -> null; // TODO: implement case for ref table
            case CLITableConstants
                .CLI_TABLE_TYPE_SPEC -> null; // TODO: implement case for spec table
            default -> throw new TypeSystemException(
                CILOSTAZOLBundle.message("typeSystem.unknownTableType"));
          };
    }

    return lazyMethods;
  }

  public MethodSymbol[] getVMT() {
    throw new NotImplementedException();
  }

  public FieldSymbol[] getFiels() {
    throw new NotImplementedException();
  }
  // endregion

  public ConstructedNamedTypeSymbol Construct(TypeSymbol[] typeArguments) {
    throw new NotImplementedException();
  }

  public TypeSymbol[] getTypeArguments() {
    return new TypeSymbol[0];
  }

  private static class LazyFactory {
    private static MethodSymbol[] createMethods(NamedTypeSymbol symbol, CLITypeDefTableRow row) {
      var methodTablePtr = row.getMethodListTablePtr();

      final boolean isLastType =
          row.getRowNo()
              == symbol
                  .getDefiningModule()
                  .getDefiningFile()
                  .getTablesHeader()
                  .getRowCount(CLITableConstants.CLI_TABLE_TYPE_DEF);
      final int lastIdx =
          isLastType
              ? symbol
                  .getDefiningModule()
                  .getDefiningFile()
                  .getTablesHeader()
                  .getRowCount(CLITableConstants.CLI_TABLE_METHOD_DEF)
              : row.skip(1).getMethodListTablePtr().getRowNo();

      var methodRow =
          symbol
              .getDefiningModule()
              .getDefiningFile()
              .getTableHeads()
              .getMethodDefTableHead()
              .skip(methodTablePtr);

      var methods = new ArrayList<MethodSymbol>();
      while (methodRow.getRowNo() < lastIdx) {
        var method = MethodSymbol.MethodSymbolFactory.create(methodRow, symbol);
        methods.add(method);
        methodRow = methodRow.next();
      }

      return methods.toArray(MethodSymbol[]::new);
    }
  }

  public static class NamedTypeSymbolFactory {
    public static NamedTypeSymbol create(
        CLITablePtr ptr, TypeSymbol[] mvars, TypeSymbol[] vars, ModuleSymbol module) {
      throw new NotImplementedException();
    }

    public static NamedTypeSymbol create(
        CLITypeSpecTableRow row, TypeSymbol[] mvars, TypeSymbol[] vars, ModuleSymbol module) {
      throw new NotImplementedException();
    }

    public static NamedTypeSymbol create(CLITypeRefTableRow row, ModuleSymbol module) {
      throw new NotImplementedException();
    }

    public static NamedTypeSymbol create(CLITypeDefTableRow row, ModuleSymbol module) {
      final String name = row.getTypeNameHeapPtr().read(module.getDefiningFile().getStringHeap());
      final String namespace =
          row.getTypeNamespaceHeapPtr().read(module.getDefiningFile().getStringHeap());
      final TypeParameterSymbol[] typeParams =
          TypeParameterSymbol.TypeParameterSymbolFactory.create(
              row.getPtr(), new TypeSymbol[0], module);
      int flags = row.getFlags();

      return new NamedTypeSymbol(module, flags, name, namespace, typeParams, row.getPtr());
    }

    public static NamedTypeSymbol create(
        TypeSig sig, TypeSymbol[] mvars, TypeSymbol[] vars, ModuleSymbol module) {
      throw new NotImplementedException();
    }

    public static NamedTypeSymbol create(
        TypeSpecSig sig, TypeSymbol[] mvars, TypeSymbol[] vars, ModuleSymbol module) {
      throw new NotImplementedException();
    }
  }
}
