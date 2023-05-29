package com.vztekoverflow.cilostazol.context;

import com.vztekoverflow.cilostazol.CILOSTAZOLLanguage;
import com.vztekoverflow.cilostazol.meta.Meta;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.GuestAllocator;

public interface ContextAccess extends LanguageAccess {
    CILOSTAZOLContext getContext();

    default CILOSTAZOLLanguage getLanguage() {
        return getContext().getLanguage();
    }

    default Meta getMeta() {
        return getContext().getMeta();
    }

    default GuestAllocator getAllocator() {
        return getContext().getAllocator();
    }
}