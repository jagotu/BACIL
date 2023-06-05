package com.vztekoverflow.cilostazol.runtime.typesystem.component;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.CLITableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.*;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.typesystem.TypeSystemException;
import com.vztekoverflow.cilostazol.runtime.typesystem.assembly.IAssembly;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.factory.TypeFactory;

public class CLIComponent implements IComponent {
    private final CLIFile _cliFile;
    private final IAssembly _definingAssembly;
    //TODO: caching

    //region IComponent
    @Override
    public CLIFile getDefiningFile() {
        return _cliFile;
    }

    @Override
    public IAssembly getDefiningAssembly() {
        return _definingAssembly;
    }

    //TODO: gettype -> if local defined then call getLocalType, if not, get the reference and call in on assembly or different module

    @Override
    public IType getLocalType(CILOSTAZOLContext context, String namespace, String name) {
        //print typeDefs
        for (CLITypeDefTableRow row : _cliFile.getTableHeads().getTypeDefTableHead()) {
            var rowName = getNameFrom(row);
            var rowNamespace = getNamespaceFrom(row);
            System.out.println(rowNamespace + "::" + rowName);
        }

        //Check typeDefs
        for (CLITypeDefTableRow row : _cliFile.getTableHeads().getTypeDefTableHead()) {
            var rowName = getNameFrom(row);
            var rowNamespace = getNamespaceFrom(row);
            if (rowNamespace.equals(namespace) && rowName.equals(name))
                return TypeFactory.create(context, row, this);
        }

        //Check exported types (II.6.8 Type forwarders)
        for(CLIExportedTypeTableRow row : _cliFile.getTableHeads().getExportedTypeTableHead())
        {
            if (row.getTypeNamespaceHeapPtr().read(_cliFile.getStringHeap()).equals(namespace) && row.getTypeNameHeapPtr().read(_cliFile.getStringHeap()).equals(name)) {
                CLIAssemblyRefTableRow assemblyRef = _cliFile.getTableHeads().getAssemblyRefTableHead().skip(row.getImplementationTablePtr());
                IAssembly assembly = getDefiningAssembly().getAppDomain().getAssembly(AssemblyIdentity.fromAssemblyRefRow(_cliFile.getStringHeap(), assemblyRef));
                return assembly.getLocalType(namespace, name);
            }
        }

        CompilerDirectives.transferToInterpreterAndInvalidate();
        throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.typesystem.typeNotFound", namespace, name));
    }

    @Override
    public IType getLocalType(CILOSTAZOLContext context, CLITablePtr tablePtr) {
        var typeDefTableRow = getTableHeads().getTypeDefTableHead().skip(tablePtr);
        return TypeFactory.create(context, typeDefTableRow, this);
    }

    public <T extends CLITableRow<T>> String getNameFrom(CLITableRow<T> row) {
        return switch (row.getTableId()) {
            case CLITableConstants.CLI_TABLE_TYPE_DEF ->
                    ((CLITypeDefTableRow) row).getTypeNameHeapPtr().read(_cliFile.getStringHeap());
            case CLITableConstants.CLI_TABLE_FIELD ->
                    ((CLIFieldTableRow) row).getNameHeapPtr().read(_cliFile.getStringHeap());
            default -> {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.typesystem.unsupportedTable", row.getTableId()));
            }
        };
    }

    public <T extends CLITableRow<T>> String getNamespaceFrom(CLITableRow<T> row) {
        return switch (row.getTableId()) {
            case CLITableConstants.CLI_TABLE_TYPE_DEF ->
                    ((CLITypeDefTableRow) row).getTypeNamespaceHeapPtr().read(_cliFile.getStringHeap());
            default -> {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.typesystem.unsupportedTable", row.getTableId()));
            }
        };
    }

    @Override
    public CLITableHeads getTableHeads() {
        return _cliFile.getTableHeads();
    }

    //endregion

    private CLIComponent(CLIFile file, IAssembly assembly) {
        _cliFile = file;
        _definingAssembly = assembly;
    }

    public static IComponent parse(CLIFile file, IAssembly assembly) {
        return new CLIComponent(file, assembly);
    }
}
