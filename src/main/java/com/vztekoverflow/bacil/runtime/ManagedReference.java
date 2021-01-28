package com.vztekoverflow.bacil.runtime;

import com.vztekoverflow.bacil.runtime.types.Type;

public class ManagedReference {
    private final Object referee;
    private final Type type;

    public ManagedReference(Object referee, Type type) {
        this.referee = referee;
        this.type = type;
    }

    public Object getReferee() {
        return referee;
    }

    public Type getType() {
        return type;
    }
}
