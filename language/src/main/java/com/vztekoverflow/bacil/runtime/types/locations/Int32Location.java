package com.vztekoverflow.bacil.runtime.types.locations;

import com.vztekoverflow.bacil.runtime.EvaluationStackPrimitiveMarker;
import com.vztekoverflow.bacil.runtime.types.Type;

public final class Int32Location extends Location {
    int value = 0;

    public Int32Location(Type type) {
        super(type);
    }

    public Int32Location(Type type, int value) {
        super(type);
        this.value = value;
    }

    @Override
    public void fromStackVar(Object ref, long primitive) {
        value = (int)primitive;
    }

    @Override
    public void toStackVar(Object[] refs, long[] primitives, int slot) {
        refs[slot] = EvaluationStackPrimitiveMarker.EVALUATION_STACK_INT32;
        primitives[slot] = value;
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public void fromObject(Object obj) {
        value = (Integer)obj;
    }

    @Override
    public Location clone() {
        return new Int32Location(type, value);
    }
}
