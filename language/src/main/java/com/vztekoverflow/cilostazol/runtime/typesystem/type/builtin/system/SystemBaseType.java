package com.vztekoverflow.cilostazol.runtime.typesystem.type.builtin.system;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.field.IField;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.IMethod;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.TypeBase;

public abstract class SystemBaseType extends TypeBase {
    public SystemBaseType(CLIFile _definingFile, String _name, String _namespace, IType _directBaseClass, IType[] _interfaces, IMethod[] _methods, IMethod[] _vMethodTable, IField[] _fields, IComponent _definingComponent) {


        super(_definingFile, _name, _namespace, _directBaseClass, _interfaces, _methods, _vMethodTable, _fields, _definingComponent);
    }
}
