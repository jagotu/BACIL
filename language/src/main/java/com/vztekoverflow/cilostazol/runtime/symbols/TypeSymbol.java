package com.vztekoverflow.cilostazol.runtime.symbols;

import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cil.parser.cli.signature.SignatureReader;
import com.vztekoverflow.cil.parser.cli.signature.TypeSig;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITableConstants;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeSpecTableRow;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.runtime.other.ContextProviderImpl;
import com.vztekoverflow.cilostazol.runtime.typesystem.TypeSystemException;

public abstract class TypeSymbol extends Symbol {
  protected final ModuleSymbol definingModule;

  public TypeSymbol(ModuleSymbol definingModule) {
    super(ContextProviderImpl.getInstance());
    this.definingModule = definingModule;
  }

  public ModuleSymbol getDefiningModule() {
    return definingModule;
  }

  public static final class TypeSymbolFactory {
    public static TypeSymbol create(
        TypeSig typeSig, TypeSymbol[] mvars, TypeSymbol[] vars, ModuleSymbol module) {
      // TODO: null reference exception might have occured here if TypeSig is not created from CLASS
      // TODO: resolve for other types (SZARRAY, GENERICINST, ...)
      return switch (typeSig.getElementType()) {
        case TypeSig.ELEMENT_TYPE_CLASS, TypeSig.ELEMENT_TYPE_VALUETYPE -> create(
            typeSig.getCliTablePtr(), mvars, vars, module);
        case TypeSig.ELEMENT_TYPE_VAR -> vars[typeSig.getIndex()];
        case TypeSig.ELEMENT_TYPE_MVAR -> mvars[typeSig.getIndex()];
        case TypeSig.ELEMENT_TYPE_GENERICINST -> {
          var genType = (NamedTypeSymbol) create(typeSig.getCliTablePtr(), mvars, vars, module);
          TypeSymbol[] typeArgs = new TypeSymbol[typeSig.getTypeArgs().length];
          for (int i = 0; i < typeArgs.length; i++) {
            typeArgs[i] = create(typeSig.getTypeArgs()[i], mvars, vars, module);
          }
          yield genType.construct(typeArgs);
        }
        case TypeSig.ELEMENT_TYPE_I4 -> module // int
            .getContext()
            .getType("Int32", "System", AssemblyIdentity.SystemPrivateCoreLib());
        case TypeSig.ELEMENT_TYPE_I8 -> module // long
            .getContext()
            .getType("Int64", "System", AssemblyIdentity.SystemPrivateCoreLib());
        case TypeSig.ELEMENT_TYPE_I2 -> module // short
            .getContext()
            .getType("Int16", "System", AssemblyIdentity.SystemPrivateCoreLib());
        case TypeSig.ELEMENT_TYPE_I1 -> module
            .getContext()
            .getType("SByte", "System", AssemblyIdentity.SystemPrivateCoreLib());
        case TypeSig.ELEMENT_TYPE_U4 -> module // uint
            .getContext()
            .getType("UInt32", "System", AssemblyIdentity.SystemPrivateCoreLib());
        case TypeSig.ELEMENT_TYPE_U8 -> module // ulong
            .getContext()
            .getType("UInt64", "System", AssemblyIdentity.SystemPrivateCoreLib());
        case TypeSig.ELEMENT_TYPE_U2 -> module
            .getContext()
            .getType("UInt16", "System", AssemblyIdentity.SystemPrivateCoreLib());
        case TypeSig.ELEMENT_TYPE_U1 -> module
            .getContext()
            .getType("Byte", "System", AssemblyIdentity.SystemPrivateCoreLib());
        case TypeSig.ELEMENT_TYPE_R4 -> module
            .getContext()
            .getType("Single", "System", AssemblyIdentity.SystemPrivateCoreLib());
        case TypeSig.ELEMENT_TYPE_R8 -> module
            .getContext()
            .getType("Double", "System", AssemblyIdentity.SystemPrivateCoreLib());
        case TypeSig.ELEMENT_TYPE_BOOLEAN -> module
            .getContext()
            .getType("Boolean", "System", AssemblyIdentity.SystemPrivateCoreLib());
        case TypeSig.ELEMENT_TYPE_CHAR -> module
            .getContext()
            .getType("Char", "System", AssemblyIdentity.SystemPrivateCoreLib());
        case TypeSig.ELEMENT_TYPE_STRING -> module
            .getContext()
            .getType("String", "System", AssemblyIdentity.SystemPrivateCoreLib());
        case TypeSig.ELEMENT_TYPE_OBJECT -> module
            .getContext()
            .getType("Object", "System", AssemblyIdentity.SystemPrivateCoreLib());
        case TypeSig.ELEMENT_TYPE_VOID -> module
            .getContext()
            .getType("Void", "System", AssemblyIdentity.SystemPrivateCoreLib());
        case TypeSig.ELEMENT_TYPE_TYPEDBYREF -> module
            .getContext()
            .getType("TypedReference", "System", AssemblyIdentity.SystemPrivateCoreLib());
        case TypeSig.ELEMENT_TYPE_I -> module
            .getContext()
            .getType("IntPtr", "System", AssemblyIdentity.SystemPrivateCoreLib());
        case TypeSig.ELEMENT_TYPE_U -> module
            .getContext()
            .getType("UIntPtr", "System", AssemblyIdentity.SystemPrivateCoreLib());
        case TypeSig.ELEMENT_TYPE_FNPTR -> module
            .getContext()
            .getType("RuntimeMethodHandle", "System", AssemblyIdentity.SystemPrivateCoreLib());
        default -> null;
      };
    }

    public static TypeSymbol create(
        CLITablePtr ptr, TypeSymbol[] mvars, TypeSymbol[] vars, ModuleSymbol module) {
      return switch (ptr.getTableId()) {
        case CLITableConstants.CLI_TABLE_TYPE_DEF -> NamedTypeSymbol.NamedTypeSymbolFactory.create(
            module.getDefiningFile().getTableHeads().getTypeDefTableHead().skip(ptr), module);
        case CLITableConstants.CLI_TABLE_TYPE_REF -> NamedTypeSymbol.NamedTypeSymbolFactory.create(
            module.getDefiningFile().getTableHeads().getTypeRefTableHead().skip(ptr), module);
        case CLITableConstants.CLI_TABLE_TYPE_SPEC -> create(
            module.getDefiningFile().getTableHeads().getTypeSpecTableHead().skip(ptr),
            mvars,
            vars,
            module);
        default -> throw new TypeSystemException(
            CILOSTAZOLBundle.message("cilostazol.exception.constructor.withoutDefType"));
      };
    }

    public static TypeSymbol create(
        CLITypeSpecTableRow row, TypeSymbol[] mvars, TypeSymbol[] vars, ModuleSymbol module) {
      TypeSig signature =
          TypeSig.read(
              new SignatureReader(
                  row.getSignatureHeapPtr().read(module.getDefiningFile().getBlobHeap())),
              module.getDefiningFile());
      return TypeSymbol.TypeSymbolFactory.create(signature, mvars, vars, module);
    }
  }
}
