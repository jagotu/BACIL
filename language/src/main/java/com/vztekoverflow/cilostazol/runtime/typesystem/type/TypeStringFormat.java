package com.vztekoverflow.cilostazol.runtime.typesystem.type;

public enum TypeStringFormat {
    AnsiClass,
    UnicodeClass,
    AutoClass,
    CustomFormatClass;

    public final static int MASK = 0x30000;
    public final static int CUSTOM_STRING_FORMAT_MASK = 0xC00000;

    public static TypeStringFormat fromFlags(int flags) {
        return TypeStringFormat.values()[(flags & MASK) >> 16];
    }
}
