package com.vztekoverflow.cilostazol.runtime.typesystem.type;

public enum TypeSemantics {
    Class,
    Interface;
    public static final int MASK = 0x20;

    public static TypeSemantics fromFlags(int flags) {
        return TypeSemantics.values()[(flags & MASK) >> 5];
    }
}
