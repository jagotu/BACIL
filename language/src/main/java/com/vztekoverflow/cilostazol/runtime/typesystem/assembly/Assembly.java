package com.vztekoverflow.cilostazol.runtime.typesystem.assembly;

import com.vztekoverflow.cil.parser.CILParserException;
import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITableConstants;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.runtime.typesystem.appdomain.IAppDomain;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.CLIComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;
import org.graalvm.polyglot.Source;

public class Assembly implements IAssembly {
    private final IComponent[] _components;
    private final CLIFile _file;

    //region IAssembly
    @Override
    public CLIFile getDefiningFile() {
        return _file;
    }

    public IComponent[] getComponents() {return _components;}

    @Override
    public IType getLocalType(String namespace, String name, IAppDomain appDomain) {
        IType result = null;
        for (int i = 0; i < _components.length && result == null; i++) {
            result = _components[i].getLocalType(namespace, name, appDomain);
        }

        return result;
    }

    @Override
    public AssemblyIdentity getIdentity() {
        return _file.getAssemblyIdentity();
    }
    //endregion

    private Assembly(CLIFile file, IComponent[] components) {
        _file = file;
        _components = components;
    }

    public static IAssembly parse(Source dllSource) {
        CLIFile file = CLIFile.parse(dllSource.getBytes());

        if (file.getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_MODULE) != 1)
            throw new CILParserException(CILOSTAZOLBundle.message("cilostazol.exception.module"));

        if (file.getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_MODULE_REF) > 0)
            throw new CILParserException(CILOSTAZOLBundle.message("cilostazol.exception.multimoduleAssembly"));

        Assembly assembly = new Assembly(file, new IComponent[]{CLIComponent.parse(file)});

        return assembly;
    }
}
