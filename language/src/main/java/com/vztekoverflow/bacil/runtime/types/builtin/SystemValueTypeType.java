package com.vztekoverflow.bacil.runtime.types.builtin;

import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITypeDefTableRow;
import com.vztekoverflow.bacil.runtime.locations.LocationsHolder;
import com.vztekoverflow.bacil.runtime.types.CLIType;
import com.vztekoverflow.bacil.runtime.types.Type;

/**
 * Implementation of the System.ValueType type.
 * Replaces the reference-style operations of {@link Type} with stubs so that children have to override them
 * with proper primitive-style handlers.
 */
public class SystemValueTypeType extends CLIType {
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
    public void objectToLocation(LocationsHolder holder, int holderOffset, Object value) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
