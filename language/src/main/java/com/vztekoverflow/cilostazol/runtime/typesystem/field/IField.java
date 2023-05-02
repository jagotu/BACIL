package com.vztekoverflow.cilostazol.runtime.typesystem.field;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public interface IField {
    public CLIFile getDefiningFile();
    public IType getType();
    public String getName();
    public boolean isStatic();
}
