package com.vztekoverflow.cilostazol.runtime.typesystem.assembly;

import com.vztekoverflow.cil.parser.CILParserException;
import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITableConstants;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.CILOSTAZOLException;
import com.vztekoverflow.cilostazol.runtime.typesystem.AppDomain;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.CILComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.Component;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.Type;
import org.graalvm.polyglot.Source;

import java.text.ParseException;

public class Assembly {
    private final Component[] _components;
    private final AssemblyIdentity _identity;
    private final CLIFile _file;
    private final AppDomain _domain;

    private Assembly(CLIFile file, Component[] components, AppDomain domain) {
        _components = components;
        _file = file;
        _identity = _file.getAssemblyIdentity();
        _domain = domain;
    }

    public AssemblyIdentity getAssemblyIdentity() {
        return _identity;
    }

    public static Assembly parseAssembly(Source dllSource, AppDomain domain) {
        CLIFile file = CLIFile.parse(dllSource.getBytes());

        if (file.getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_MODULE) != 1)
            throw new CILParserException(CILOSTAZOLBundle.message("cilostazol.exception.module"));

        if (file.getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_MODULE_REF) > 0)
            throw new CILParserException(CILOSTAZOLBundle.message("cilostazol.exception.multimoduleAssembly"));

        Assembly assembly = new Assembly(file, new Component[]{CILComponent.parse(dllSource, domain)}, domain);

        domain.loadAssembly(assembly);
        return assembly;
    }

    public Component[] getComponents() {return _components;}

    public Type findLocalType(String namespace, String name) {
        //TODO
        return null;
    }
}
