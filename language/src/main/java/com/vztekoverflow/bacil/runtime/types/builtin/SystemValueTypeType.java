package com.vztekoverflow.bacil.runtime.types.builtin;

import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITypeDefTableRow;
import com.vztekoverflow.bacil.runtime.types.NamedType;
import com.vztekoverflow.bacil.runtime.types.locations.LocationsHolder;

public class SystemValueTypeType extends NamedType {
    public SystemValueTypeType(CLITypeDefTableRow type, CLIComponent component) {
        super(type, component);
    }

    @Override
    public boolean isPrimitiveStorage() {
        return true;
    }

    @Override
    public Object initialValue() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void stackToLocation(LocationsHolder holder, int holderOffset, Object ref, long primitive) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void locationToStack(LocationsHolder holder, int holderOffset, Object[] refs, long[] primitives, int slot) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Object stackToObject(Object ref, long primitive) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void objectToStack(Object[] refs, long[] primitives, int slot, Object value) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Object locationToObject(LocationsHolder holder, int holderOffset) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void objectToLocation(LocationsHolder holder, int holderOffset, Object obj) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
