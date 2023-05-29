package com.vztekoverflow.cilostazol.runtime.typesystem.field;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitutable;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public interface IField extends ISubstitutable<IField, IType> {
    public CLIFile getDefiningFile();
    public IType getType();
    public String getName();
    public boolean isStatic();

    public Class<?> getPropertyType();
    public boolean isFinal();
}
