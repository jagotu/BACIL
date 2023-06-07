package com.vztekoverflow.cilostazol.runtime.objectmodel;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.interop.TruffleObject;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public class StaticObject implements TruffleObject, Cloneable {
    private final IType type;
    public static final StaticObject[] EMPTY_ARRAY = new StaticObject[0];
    public static final StaticObject NULL = new StaticObject(null);

    protected StaticObject(IType type) {
        this.type = type;
    }

    @Override
    @CompilerDirectives.TruffleBoundary
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public static boolean isNull(StaticObject object) {
        assert object != null;
        assert (object.getType() != null) || object == NULL: "Klass can only be null for NULL object";
        return object.getType() == null;
    }

    public static boolean notNull(StaticObject object) {
        return !isNull(object);
    }

    public final IType getType() {
        return type;
    }

    public final boolean isArray() {
        return !isNull(this) && getType().isArray();
    }

    public interface StaticObjectFactory {
        StaticObject create(IType type);
    }
}
