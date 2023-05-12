package com.vztekoverflow.cilostazol.runtime.typesystem.field;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitution;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public class Field implements IField {

    private final String name;
    private final IType type;
    private final boolean isStatic;
    private final CLIFile definingFile;

    public Field(String name, IType type, boolean isStatic, CLIFile definingFile) {
        this.name = name;
        this.type = type;
        this.isStatic = isStatic;
        this.definingFile = definingFile;
    }

    @Override
    public CLIFile getDefiningFile() {
        return definingFile;
    }

    @Override
    public IType getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public IField substitute(ISubstitution<IType> substitution) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public IField getDefinition() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public IField getConstructedFrom() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
