package com.vztekoverflow.cilostazol.runtime.typesystem;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITableConstants;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITypeDefTableRow;
import com.vztekoverflow.bacil.runtime.types.CLIType;
import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cil.parser.cli.CLIComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.Type;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.TypeFactory;
import org.graalvm.polyglot.io.ByteSequence;

public class CILComponent extends Component {
    private final CLIComponent _cliComponent;
    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private final Type[] _localDefTypes;

    private CILComponent(CLIComponent component){
        _cliComponent = component;
        _localDefTypes = new Type[component.getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_TYPE_DEF)];
    }

    public static CILComponent parse(ByteSequence bytes) {
        CLIComponent component = CLIComponent.parseComponent(bytes);

        return new CILComponent(component);
    }

    @Override
    public AssemblyIdentity getAssemblyIdentity() {
        return _cliComponent.getAssemblyIdentity();
    }

    @Override
    public Type findLocalType(String namespace, String name) {
        return null;
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
}
