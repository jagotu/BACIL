package com.vztekoverflow.cilostazol.objectmodel;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.interop.TruffleObject;
import com.vztekoverflow.cilostazol.CILOSTAZOLEngineOption;
import com.vztekoverflow.cilostazol.context.ContextAccessImpl;
import com.vztekoverflow.cilostazol.meta.ModifiersProvider;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;

public abstract class Klass extends ContextAccessImpl implements ModifiersProvider, TruffleObject {

    @CompilerDirectives.CompilationFinal
    private StaticObject espressoClass;

    @CompilerDirectives.CompilationFinal //
    private ArrayKlass arrayKlass;

    private final long id;

    private final int modifiers;

    Klass(CILOSTAZOLContext context, int modifiers) {
        this(context, modifiers, -1);
    }

    Klass(CILOSTAZOLContext context, int modifiers, long possibleID) {
        super(context);
        // TODO:
        this.id = (possibleID >= 0) ? possibleID : getNewId();
        this.modifiers = modifiers;
    }

    public final boolean isArray() {
        return this instanceof ArrayKlass;
    }

    public Object getKlassObject() {
        return mirror();
    }

    /**
     * Returns the guest {@link Class} object associated with this {@link Klass} instance.
     */
    public final StaticObject mirror() {
        StaticObject result = this.espressoClass;
        assert result != null;
        return result;
    }

    public final StaticObject initializeCilostazolClass() {
        CompilerAsserts.neverPartOfCompilation();
        StaticObject result = this.espressoClass;
        if (result == null) {
            synchronized (this) {
                result = this.espressoClass;
                if (result == null) {
                    this.espressoClass = result = getAllocator().createClass(this);
                }
            }
        }
        return result;
    }

    public final ArrayKlass array() {
        return getArrayClass();
    }

    public final ArrayKlass getArrayClass() {
        ArrayKlass result = this.arrayKlass;
        if (result == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            result = createArrayKlass();
        }
        return result;
    }

    private synchronized ArrayKlass createArrayKlass() {
        CompilerAsserts.neverPartOfCompilation();
        ArrayKlass result = this.arrayKlass;
        if (result == null) {
            this.arrayKlass = result = new ArrayKlass(this);
        }
        return result;
    }

    public ArrayKlass getArrayClass(int dimensions) {
        assert dimensions > 0;
        ArrayKlass array = array();

        // Careful with of impossible void[].
        if (array == null) {
            return null;
        }

        for (int i = 1; i < dimensions; ++i) {
            array = array.getArrayClass();
        }
        return array;
    }

    public abstract Klass getElementalType();

    @Override
    public final int getModifiers() {
        return modifiers;
    }

    public abstract int getClassModifiers();

    public Assumption getRedefineAssumption() {
        return Assumption.ALWAYS_VALID;
    }

    private long getNewId() {
        return 255;
    }
}
