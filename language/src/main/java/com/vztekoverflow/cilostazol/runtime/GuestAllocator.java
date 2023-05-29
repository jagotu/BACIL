package com.vztekoverflow.cilostazol.runtime;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.instrumentation.AllocationReporter;
import com.vztekoverflow.cilostazol.CILOSTAZOLLanguage;
import com.vztekoverflow.cilostazol.context.LanguageAccess;
import com.vztekoverflow.cilostazol.objectmodel.ArrayKlass;
import com.vztekoverflow.cilostazol.objectmodel.Klass;
import com.vztekoverflow.cilostazol.objectmodel.ObjectKlass;
import com.vztekoverflow.cilostazol.objectmodel.StaticObject;

public final class GuestAllocator implements LanguageAccess {
    private final CILOSTAZOLLanguage language;
    private final AllocationReporter allocationReporter;

    public GuestAllocator(CILOSTAZOLLanguage language, AllocationReporter allocationReporter) {
        this.language = language;
        this.allocationReporter = allocationReporter;
        if (allocationReporter != null) {
            // Can be already active, in which case the active value change notification is missed.
            if (allocationReporter.isActive()) {
                getLanguage().invalidateAllocationTrackingDisabled();
            }
            allocationReporter.addActiveListener((isActive) -> {
                if (isActive) {
                    getLanguage().invalidateAllocationTrackingDisabled();
                }
            });
        }
    }

    @Override
    public CILOSTAZOLLanguage getLanguage() {
        return language;
    }

    public StaticObject createClass(Klass klass) {
        assert klass != null;
        CompilerAsserts.neverPartOfCompilation();
        ObjectKlass guestClass = klass.getMeta().system_Type;
        StaticObject newObj = guestClass.getLinkedKlass().getShape(false).getFactory().create(guestClass);
        initInstanceFields(newObj, guestClass);

        klass.getMeta().java_lang_Class_classLoader.setObject(newObj, klass.getDefiningClassLoader());
        if (klass.getContext().getJavaVersion().modulesEnabled()) {
            setModule(newObj, klass);
        }
        // The Class.componentType field is only available on 9+.
        if (klass.isArray() && klass.getMeta().java_lang_Class_componentType != null) {
            klass.getMeta().java_lang_Class_componentType.setObject(newObj, ((ArrayKlass) klass).getComponentType().initializeEspressoClass());
        }
        // Will be overriden if necessary, but should be initialized to non-host null.
        klass.getMeta().HIDDEN_PROTECTION_DOMAIN.setHiddenObject(newObj, StaticObject.NULL);
        // Final hidden field assignment
        klass.getMeta().HIDDEN_MIRROR_KLASS.setHiddenObject(newObj, klass);
        return trackAllocation(klass, newObj);
    }
}