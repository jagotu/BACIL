package com.vztekoverflow.cilostazol.runtime.typesystem.component;

import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cil.parser.cli.table.CLIStringHeapPtr;
import com.vztekoverflow.cil.parser.cli.table.generated.CLIAssemblyRefTableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.CLIExportedTypeTableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITableHeads;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeDefTableRow;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
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
    public IType getLocalType(String namespace, String name) {
        //PRINT TYPE DEFS
        for (CLITypeDefTableRow row : _cliFile.getTableHeads().getTypeDefTableHead()) {
            var rowNamespace = row.getTypeNamespaceStringHeapPtr().read(_cliFile.getStringHeap());
            var rowName = row.getTypeNameStringHeapPtr().read(_cliFile.getStringHeap());
            System.out.println(rowNamespace + "." + rowName);
        }

        //Check typeDefs
        for (CLITypeDefTableRow row : _cliFile.getTableHeads().getTypeDefTableHead()) {
            var rowNamespace = row.getTypeNamespaceStringHeapPtr().read(_cliFile.getStringHeap());
            var rowName = row.getTypeNameStringHeapPtr().read(_cliFile.getStringHeap());
            if (rowNamespace.equals(namespace) && rowName.equals(name))
                return TypeFactory.create(row, _cliFile.getTableHeads().getInterfaceImplTableHead(), null, null, this);
        }

        //Check exported types (II.6.8 Type forwarders)
        for(CLIExportedTypeTableRow row : _cliFile.getTableHeads().getExportedTypeTableHead())
        {
            if (row.getTypeNamespace().read(_cliFile.getStringHeap()).equals(namespace) && row.getTypeName().read(_cliFile.getStringHeap()).equals(name)) {
                CLIAssemblyRefTableRow assemblyRef = _cliFile.getTableHeads().getAssemblyRefTableHead().skip(row.getImplementation());
                IAssembly assembly = getDefiningAssembly().getAppDomain().getAssembly(AssemblyIdentity.fromAssemblyRefRow(_cliFile.getStringHeap(), assemblyRef));
                return assembly.getLocalType(namespace, name);
            }
        }

        CompilerDirectives.transferToInterpreterAndInvalidate();
        throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.typesystem.typeNotFound", namespace, name));
    }

    @Override
    public IType getLocalType(int typeIndex) {
        // The first row of the TypeDef table represents the pseudo class that acts as parent for functions and variables defined at module scope.
        final int pseudoClassOnFirstRowOffset = 1;
        // Can only be indexed into TypeDef table
        var typeDefTableRow = _cliFile.getTableHeads().getTypeDefTableHead().skip(typeIndex - pseudoClassOnFirstRowOffset);
        return TypeFactory.create(typeDefTableRow, _cliFile.getTableHeads().getInterfaceImplTableHead(), null, null, this);
    }

    @Override
    public String getTypeName(CLIStringHeapPtr ptr) {
        return ptr.read(_cliFile.getStringHeap());
    }

    @Override
    public String getTypeNamespace(CLIStringHeapPtr ptr) {
        return ptr.read(_cliFile.getStringHeap());
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
