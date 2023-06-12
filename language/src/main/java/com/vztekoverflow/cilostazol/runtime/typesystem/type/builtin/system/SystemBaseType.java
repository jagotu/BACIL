package com.vztekoverflow.cilostazol.runtime.typesystem.type.builtin.system;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.CLIComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.TypeBase;

public abstract class SystemBaseType extends TypeBase<CLITableSystemTypeRow> {
    public SystemBaseType(CLIFile _definingFile, String _name, String _namespace, IType _directBaseClass, IType[] _interfaces, CLIComponent _definingComponent) {
        super(CILOSTAZOLContext.get(null), null, _definingFile, _name, _namespace, _directBaseClass, _interfaces, _definingComponent, 0);
    }
}
