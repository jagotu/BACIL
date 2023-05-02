package com.vztekoverflow.cilostazol.runtime.typesystem.method;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitutable;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitution;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public interface IMethod extends ISubstitutable<IMethod, IType> {
    public CLIFile getDefiningFile();
    public String getName();
    public boolean hasThis();
    public boolean hasExplicitThis();
    public boolean hasVarArg();
    public boolean isVirtual();
    public IParameter[] getParameters();
    public IParameter[] getLocals();
    public ITypeParameter[] getTypeParameters();
    public IParameter getReturnType();
    public IParameter getThis();
    public IExceptionHandler[] getExceptionHandlers();
    public IComponent getDefiningComponent();
    public IType getDefiningType();

    public IMethod substitute(ISubstitution<IType> substitution);
    public IMethod getDefinition();
    public IMethod getConstructedFrom();
    //TODO: attributes, ExecuteNode
}
