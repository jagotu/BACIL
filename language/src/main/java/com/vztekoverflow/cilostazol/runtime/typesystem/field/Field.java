package com.vztekoverflow.cilostazol.runtime.typesystem.field;

import com.oracle.truffle.api.staticobject.StaticProperty;
import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.meta.SystemTypes;
import com.vztekoverflow.cilostazol.objectmodel.StaticObject;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.ISubstitution;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public class Field extends StaticProperty implements IField {

    private final String name;
    private final IType type;
    private final boolean isStatic;
    private final CLIFile definingFile;
    private final int slot;

    public Field(String name, IType type, boolean isStatic, CLIFile definingFile) {
        this.name = name;
        this.type = type;
        this.isStatic = isStatic;
        this.definingFile = definingFile;

        // TODO:
        this.slot = -1;
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

    @Override
    protected String getId() {
        return getName();
    }

    @Override
    public Class<?> getPropertyType() {
        return switch (type.getKind()) {
            case Boolean -> boolean.class;
            case Char -> char.class;
            case Float -> float.class;
            case Double -> double.class;
            case Int -> int.class;
            case Long -> long.class;
            default -> StaticObject.class;
        };
    }

    @Override
    public boolean isFinal() {
        return false;
    }
}
