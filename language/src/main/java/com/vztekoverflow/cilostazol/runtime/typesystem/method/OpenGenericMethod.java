package com.vztekoverflow.cilostazol.runtime.typesystem.method;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cil.parser.cli.signature.MethodDefFlags;
import com.vztekoverflow.cilostazol.nodes.CILOSTAZOLRootNode;
import com.vztekoverflow.cilostazol.runtime.typesystem.TypeSystemException;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.CLIComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitution;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ITypeParameter;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.exceptionhandler.IExceptionHandler;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.flags.MethodFlags;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.flags.MethodHeaderFlags;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.flags.MethodImplFlags;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.local.ILocal;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.parameter.IParameter;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.returnType.IReturnType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public class OpenGenericMethod extends MethodBase implements ICILBasedMethod {
    protected final ITypeParameter[] _typeParameters;

    public OpenGenericMethod(
            CLIFile definingFile,
            String name,
            CLIComponent definingComponent,
            IType definingType,
            MethodDefFlags methodDefFlags,
            MethodFlags methodFlags,
            MethodImplFlags methodImplFlags,
            MethodHeaderFlags methodHeaderFlags,
            IParameter[] parameters,
            ILocal[] locals,
            IReturnType retType,
            IExceptionHandler[] exceptionHandlers,
            byte[] cil,
            int maxStack,
            ITypeParameter[] typeParameters)
    {
        super(
                definingFile,
                name,
                definingComponent,
                definingType,
                methodDefFlags,
                methodFlags,
                methodImplFlags,
                methodHeaderFlags, parameters,
                locals,
                retType,
                exceptionHandlers,
                cil,
                maxStack);
        _typeParameters = typeParameters;
    }

    //region MethodBase
    @Override
    public ITypeParameter[] getTypeParameters() {
        return _typeParameters;
    }

    @Override
    public IMethod substitute(ISubstitution<IType> substitution) {
        return new SubstitutedGenericMethod(this, this, substitution);
    }

    @Override
    public IMethod getConstructedFrom() {
        return this;
    }

    @Override
    public CILOSTAZOLRootNode getNode() {
        throw new TypeSystemException("cilostazol.exception.invalidOperation");
    }
    //endregion
}
