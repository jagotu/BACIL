package com.vztekoverflow.bacil.runtime;

import com.vztekoverflow.bacil.runtime.types.Type;

public interface ManagedReference {

    public Object getValue();
    public void setValue(Object object);
    public Type getType();

}
