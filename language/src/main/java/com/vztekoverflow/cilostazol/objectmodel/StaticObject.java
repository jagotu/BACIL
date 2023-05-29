package com.vztekoverflow.cilostazol.objectmodel;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.interop.TruffleObject;

public class StaticObject implements TruffleObject, Clonable {
    private final Klass klass; // != ValueKlass
    public static final StaticObject[] EMPTY_ARRAY = new StaticObject[0];
    public static final StaticObject NULL = new StaticObject(null);

    protected StaticObject(Klass klass) {
        this.klass = klass;
    }

    @Override
    @CompilerDirectives.TruffleBoundary
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public static boolean isNull(StaticObject object) {
        assert object != null;
        assert (object.getKlass() != null) || object == NULL: "Klass can only be null for NULL object";
        return object.getKlass() == null;
    }

    public static boolean notNull(StaticObject object) {
        return !isNull(object);
    }

    public final Klass getKlass() {
        return klass;
    }

    public final boolean isArray() {
        return !isNull(this) && getKlass().isArray();
    }

    public interface StaticObjectFactory {
        StaticObject create(Klass klass);

        StaticObject create(Klass klass, boolean isForeign);
    }
}
