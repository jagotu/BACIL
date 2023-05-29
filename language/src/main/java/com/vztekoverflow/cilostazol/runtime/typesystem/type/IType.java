package com.vztekoverflow.cilostazol.runtime.typesystem.type;

import com.vztekoverflow.cilostazol.meta.SystemTypes;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.field.IField;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitutable;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitution;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.IMethod;

public interface IType extends ISubstitutable<IType, IType> {
    public IType[] getTypeParameters();
    public String getName();
    public String getNamespace();
    public IType getDirectBaseClass();
    public IType[] getInterfaces();
    public IMethod[] getMethods();
    public IMethod[] getVTable();
    public IField[] getFields();
    public IComponent getDefiningComponent();
    public SystemTypes getKind();
    public IType substitute(ISubstitution<IType> substitution);
    public IType getDefinition();
    public IType getConstructedFrom();
    public boolean isArray();
    //TODO: Support for nested classes, events, delegates, attributes, properties, return  SOM
}
