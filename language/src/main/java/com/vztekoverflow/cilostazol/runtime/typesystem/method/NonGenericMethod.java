package com.vztekoverflow.cilostazol.runtime.typesystem.method;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.runtime.typesystem.TypeSystemException;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitution;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public class NonGenericMethod extends MethodBase {

    public NonGenericMethod(CLIFile _definingFile, String _name, boolean _hasThis, boolean _hasExplicitType, boolean _hasVarArg, boolean _isVirtual, IParameter[] _parameters, IParameter[] _locals, IParameter _retType, IParameter _this, IExceptionHandler[] _exceptionHandlers, IComponent _definingComponent, IType _definingType) {
        super(_definingFile, _name, _hasThis, _hasExplicitType, _hasVarArg, _isVirtual, _parameters, _locals, _retType, _this, _exceptionHandlers, _definingComponent, _definingType);
    }

    //region MethodBase
    @Override
    public ITypeParameter[] getTypeParameters() {
        throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.invalidOperation"));
    }

    @Override
    public IMethod substitute(ISubstitution<IType> substitution) {
        throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.invalidOperation"));
    }

    @Override
    public IMethod getConstructedFrom() {
        throw new TypeSystemException(CILOSTAZOLBundle.message("cilostazol.exception.invalidOperation"));
    }
    //endregion
}
