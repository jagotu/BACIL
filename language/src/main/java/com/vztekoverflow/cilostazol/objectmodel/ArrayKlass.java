package com.vztekoverflow.cilostazol.objectmodel;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CompilerDirectives;

public final class ArrayKlass extends Klass {

    private final Klass componentType;
    private final Klass elementalType;
    private final int dimension;
    @CompilerDirectives.CompilationFinal
    private Assumption redefineAssumption;

    ArrayKlass(Klass componentType) {
        super(componentType.getContext(), componentType.getElementalType().getModifiers());
        this.componentType = componentType;
        this.elementalType = componentType.getElementalType();
        // TODO:
        this.dimension = getDim();
        this.redefineAssumption = componentType.getRedefineAssumption();
        assert getMeta().system_Type != null;
        initializeCilostazolClass();
    }


    @Override
    public Klass getElementalType() {
        return elementalType;
    }

    @Override
    public int getClassModifiers() {
        return getElementalType().getClassModifiers();
    }

    private int getDim() {
        return 1;
    }
}
