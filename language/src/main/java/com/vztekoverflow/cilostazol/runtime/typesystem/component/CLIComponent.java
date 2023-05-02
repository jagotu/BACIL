package com.vztekoverflow.cilostazol.runtime.typesystem.component;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cil.parser.cli.table.generated.CLIAssemblyRefTableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.CLIExportedTypeTableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITableConstants;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeDefTableRow;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;
import com.vztekoverflow.cilostazol.runtime.typesystem.TypeSystemException;
import com.vztekoverflow.cilostazol.runtime.typesystem.appdomain.IAppDomain;
import com.vztekoverflow.cilostazol.runtime.typesystem.assembly.IAssembly;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.ITypeFactory;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.TypeFactory;

public class CLIComponent implements IComponent {
    private final CLIFile _cliFile;
    private final IType[] _localDefTypes;
    private final ITypeFactory _typeFactory;

    //region IComponent
    @Override
    public CLIFile getDefiningFile() {
        return _cliFile;
    }

    @Override
    public IType getLocalType(String namespace, String name, IAppDomain appDomain) {
        //Check typeDefs
        for(CLITypeDefTableRow row : _cliFile.getTableHeads().getTypeDefTableHead())
        {
            if(row.getTypeNamespace().read(_cliFile.getStringHeap()).equals(namespace) && row.getTypeName().read(_cliFile.getStringHeap()).equals(name))
                return getLocalType(row);
        }

        //Check exported types (II.6.8 Type forwarders)
        for(CLIExportedTypeTableRow row : _cliFile.getTableHeads().getExportedTypeTableHead())
        {
            if(row.getTypeNamespace().read(_cliFile.getStringHeap()).equals(namespace) && row.getTypeName().read(_cliFile.getStringHeap()).equals(name))
            {
                CLIAssemblyRefTableRow assemblyRef = _cliFile.getTableHeads().getAssemblyRefTableHead().skip(row.getImplementation());
                IAssembly assembly = appDomain.getAssembly(AssemblyIdentity.fromAssemblyRefRow(_cliFile.getStringHeap(), assemblyRef));
                return assembly.getLocalType(namespace, name, appDomain);
            }
        }

        CompilerDirectives.transferToInterpreterAndInvalidate();
        throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.typesystem.typeNotFound", namespace, name));
    }

    private IType getLocalType(CLITypeDefTableRow typeDef)
    {
        if(_localDefTypes[typeDef.getRowNo()-1] == null)
        {
            CompilerAsserts.neverPartOfCompilation();
            _localDefTypes[typeDef.getRowNo()-1] = _typeFactory.create(typeDef);
        }
        return _localDefTypes[typeDef.getRowNo()-1];
    }
    //endregion

    private CLIComponent(CLIFile file) {
        _cliFile = file;
        _localDefTypes = new IType[file.getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_TYPE_DEF)];
        _typeFactory = new TypeFactory(file);
    }

    public static IComponent parse(CLIFile file) {
        return new CLIComponent(file);
    }
}
