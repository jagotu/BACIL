package com.vztekoverflow.cilostazol.runtime.typesystem.component;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cil.parser.cli.CLIComponent;
import com.vztekoverflow.cil.parser.cli.table.generated.*;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.runtime.typesystem.AppDomain;
import com.vztekoverflow.cilostazol.runtime.typesystem.TypeSystemException;
import com.vztekoverflow.cilostazol.runtime.typesystem.assembly.Assembly;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.Type;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.TypeFactory;
import org.graalvm.polyglot.io.ByteSequence;

public class CILComponent extends Component {
    private final CLIComponent _cliComponent;
    private final AppDomain _domain;
    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private final Type[] _localDefTypes;
    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private final Type[] _localSpecTypes;

    private CILComponent(CLIComponent component, AppDomain domain){
        _cliComponent = component;
        _localDefTypes = new Type[component.getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_TYPE_DEF)];
        _localSpecTypes = new Type[component.getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_TYPE_SPEC)];
        _domain = domain;
    }

    public static CILComponent parse(ByteSequence bytes, AppDomain domain) {
        CLIComponent component = CLIComponent.parseComponent(bytes);

        return new CILComponent(component, domain);
    }

    @Override
    public AssemblyIdentity getAssemblyIdentity() {
        return _cliComponent.getAssemblyIdentity();
    }

    @Override
    public Type findLocalType(String namespace, String name) {
        //Check typeDefs
        for(CLITypeDefTableRow row : _cliComponent.getTableHeads().getTypeDefTableHead())
        {
            if(row.getTypeNamespace().read(_cliComponent.getStringHeap()).equals(namespace) && row.getTypeName().read(_cliComponent.getStringHeap()).equals(name))
                return getLocalType(row);
        }

        //Check exported types (II.6.8 Type forwarders)
        for(CLIExportedTypeTableRow row : _cliComponent.getTableHeads().getExportedTypeTableHead())
        {
            if(row.getTypeNamespace().read(_cliComponent.getStringHeap()).equals(namespace) && row.getTypeName().read(_cliComponent.getStringHeap()).equals(name))
            {
                CLIAssemblyRefTableRow assemblyRef = _cliComponent.getTableHeads().getAssemblyRefTableHead().skip(row.getImplementation());
                Assembly assembly = _domain.getAssembly(AssemblyIdentity.fromAssemblyRefRow(_cliComponent.getStringHeap(), assemblyRef));
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
