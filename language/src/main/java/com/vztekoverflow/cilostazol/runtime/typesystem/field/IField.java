package com.vztekoverflow.cilostazol.runtime.typesystem.field;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.meta.SystemTypes;
import com.vztekoverflow.cilostazol.objectmodel.StaticObject;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitutable;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public interface IField extends ISubstitutable<IField, IType> {
    CLIFile getDefiningFile();

    IType getType();

    String getName();

    boolean isStatic();

    Class<?> getPropertyType();

    boolean isFinal();

    SystemTypes getKind();

    void setObjectValue(StaticObject obj, Object value);
}
