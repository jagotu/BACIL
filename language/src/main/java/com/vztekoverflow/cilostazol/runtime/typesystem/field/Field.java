package com.vztekoverflow.cilostazol.runtime.typesystem.field;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitution;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public class Field implements IField {

    private final String name;
    private final IType type;
    private final boolean isStatic;
    private final boolean isInitOnly;
    private final boolean isLiteral;
    private final boolean isNotSerialized;
    private final boolean isSpecialName;
    private final int flags;
    private final CLIFile definingFile;

    public Field(String name, IType type, boolean isStatic, boolean isInitOnly, boolean isLiteral, boolean isNotSerialized, boolean isSpecialName, int flags, CLIFile definingFile) {
        this.name = name;
        this.type = type;
        this.isStatic = isStatic;
        this.isInitOnly = isInitOnly;
        this.isLiteral = isLiteral;
        this.isNotSerialized = isNotSerialized;
        this.isSpecialName = isSpecialName;
        this.flags = flags;
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

    @Override
    public FieldVisibility getVisibility() {
        return FieldVisibility.fromFlags(flags);
    }

    @Override
    public boolean isInitOnly() {
        return isInitOnly;
    }

    @Override
    public boolean isSpecialName() {
        return isSpecialName;
    }

    @Override
    public boolean isNotSerialized() {
        return isNotSerialized;
    }

    @Override
    public boolean isLiteral() {
        return isLiteral;
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }
}
