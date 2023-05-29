package com.vztekoverflow.cilostazol.objectmodel;

import com.oracle.truffle.api.staticobject.StaticProperty;
import com.vztekoverflow.cilostazol.meta.SystemTypes;
import com.vztekoverflow.cilostazol.runtime.typesystem.field.IField;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public class LinkedField extends StaticProperty {
    private final IField parserField;
    private final int slot;

    LinkedField(IField parserField, int slot) {
        this.parserField = parserField;
        this.slot = slot;
    }

    /**
     * This method is required by the Static Object Model. In Espresso we should rather call
     * `getName()` and use Symbols.
     */
    @Override
    protected String getId() {
        return parserField.getName();
    }

    /**
     * The slot is the position in the `fieldTable` of the ObjectKlass.
     */
    public int getSlot() {
        return slot;
    }

    public IType getType() {
        return getParserField().getType();
    }

    public SystemTypes getKind() {
        return parserField.getType().getKind();
    }

    IField getParserField() {
        return parserField;
    }
}
