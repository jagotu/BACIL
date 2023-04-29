package com.vztekoverflow.cilostazol.runtime.typesystem.component;

import com.vztekoverflow.cilostazol.runtime.typesystem.AppDomain;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.Type;

public abstract class Component {

    /**
     * Get the identity of this assembly, including name and version.
     */
    public abstract AppDomain getAppDomain();

    /**
     * Get a type defined by this component.
     */
    public abstract Type findLocalType(String namespace, String name);
}
