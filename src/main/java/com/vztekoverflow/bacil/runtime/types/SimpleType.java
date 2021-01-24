package com.vztekoverflow.bacil.runtime.types;


//types that don't have any further variability
public class SimpleType extends Type {

    public static final SimpleType TYPEDBYREF = new SimpleType(Type.ELEMENT_TYPE_TYPEDBYREF);
    public static final SimpleType VOID = new SimpleType(Type.ELEMENT_TYPE_VOID);

    private final byte typeCategory;

    public SimpleType(byte typeCategory) {
        this.typeCategory = typeCategory;
    }

    @Override
    public byte getTypeCategory() {
        return typeCategory;
    }


}
