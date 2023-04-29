package com.vztekoverflow.cilostazol.runtime.typesystem.component;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.source.Source;
import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cil.parser.cli.table.generated.*;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.runtime.typesystem.AppDomain;
import com.vztekoverflow.cilostazol.runtime.typesystem.TypeSystemException;
import com.vztekoverflow.cilostazol.runtime.typesystem.assembly.Assembly;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.Type;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.TypeFactory;
import org.graalvm.polyglot.io.ByteSequence;

public class CILComponent extends Component {
    private final CLIFile _cliFile;
    private final AppDomain _domain;
    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private final Type[] _localDefTypes;
    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private final Type[] _localSpecTypes;

    private CILComponent(CLIFile component, AppDomain domain){
        _cliFile = component;
        _localDefTypes = new Type[component.getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_TYPE_DEF)];
        _localSpecTypes = new Type[component.getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_TYPE_SPEC)];
        _domain = domain;
    }

    public static CILComponent parse(Source dllSource, AppDomain domain) {
        CLIFile component = CLIFile.parseComponent(dllSource.getBytes());

        return new CILComponent(component, domain);
    }

    @Override
    public AppDomain getAppDomain() {
        return _domain;
    }

    @Override
    public Type findLocalType(String namespace, String name) {
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
                Assembly assembly = _domain.getAssembly(AssemblyIdentity.fromAssemblyRefRow(_cliFile.getStringHeap(), assemblyRef));
                return assembly.findLocalType(namespace, name);
            }
        }

        CompilerDirectives.transferToInterpreterAndInvalidate();
        throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.typesystem.typeNotFound", namespace, name));
    }

    /***
     * Get a local type defined in the TypeDef table.
     * @param typeDef the typeDef table row
     * @return the {@link com.vztekoverflow.bacil.runtime.types.Type} representing the type
     */
    private Type getLocalType(CLITypeDefTableRow typeDef)
    {
        if(_localDefTypes[typeDef.getRowNo()-1] == null)
        {
            CompilerAsserts.neverPartOfCompilation();
            _localDefTypes[typeDef.getRowNo()-1] = TypeFactory.createType(typeDef);
        }
        return _localDefTypes[typeDef.getRowNo()-1];
    }

    /***
     * Get a local type defined in the TypeSpec table.
     * @param typeSpec the typeSpec table row
     * @return the {@link com.vztekoverflow.bacil.runtime.types.Type} representing the type
     */
    public Type getLocalType(CLITypeSpecTableRow typeSpec)
    {

        //TODO
        return null;

    }
}
