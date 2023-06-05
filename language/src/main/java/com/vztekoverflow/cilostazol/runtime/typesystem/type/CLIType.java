package com.vztekoverflow.cilostazol.runtime.typesystem.type;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.context.ContextAccessImpl;
import com.vztekoverflow.cilostazol.meta.ModifiersProvider;
import com.vztekoverflow.cilostazol.meta.SystemTypes;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.CLIComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;

public abstract class CLIType extends ContextAccessImpl implements IType, ModifiersProvider {

    private SystemTypes kind;

    public abstract CLIComponent getCLIComponent();

    protected abstract int getHierarchyDepth();

    // index 0 is Object, index hierarchyDepth is this
    protected abstract CLIType[] getSuperTypes();

    public CLIFile getDefiningFile() {
        return getCLIComponent().getDefiningFile();
    }

    @Override
    public IComponent getDefiningComponent() {
        return getCLIComponent();
    }

    @Override
    public SystemTypes getKind() {
        return kind;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public IType getType() {
        return CLIType.this;
    }

    public boolean isAssignableFrom(CLIType other) {
        if (this == other) return true;

        if (this.isArray()) {
            if (other.isArray()) {
                TODO:
                return false; //((ArrayKlass) this).arrayTypeChecks((ArrayKlass) other);
            }
        }

        if (this.isInterface()) {
            return checkInterfaceSubclassing(other);
        }
        return checkOrdinaryClassSubclassing(other);
    }

    /**
     * Performs type checking for non-interface, non-array classes.
     *
     * @param other the class whose type is to be checked against {@code this}
     * @return true if {@code other} is a subclass of {@code this}
     */
    public boolean checkOrdinaryClassSubclassing(CLIType other) {
        int depth = getHierarchyDepth();
        return other.getHierarchyDepth() >= depth && other.getSuperTypes()[depth] == this;
    }

    /**
     * Performs type checking for interface classes.
     *
     * @param other the class whose type is to be checked against {@code this}
     * @return true if {@code this} is a super interface of {@code other}
     */
    public boolean checkInterfaceSubclassing(CLIType other) {
        // TODO: Are these interfaces transitive?
        IType[] interfaces = other.getInterfaces();
        return fastLookup(this, interfaces) >= 0;
    }

    protected static int fastLookup(CLIType target, IType[] types) {
        if (!CompilerDirectives.isPartialEvaluationConstant(types)) {
            return fastLookupBoundary(target, types);
        }
        // PE-friendly.
        CompilerAsserts.partialEvaluationConstant(types);
        return fastLookupImpl(target, types);
    }

    @ExplodeLoop(kind = ExplodeLoop.LoopExplosionKind.FULL_EXPLODE_UNTIL_RETURN)
    protected static int fastLookupImpl(CLIType target, IType[] types) {
        for (int i = 0; i < types.length; i++) {
            if (types[i].getType() == target) {
                return i;
            }
        }

        return -1;
    }

    @CompilerDirectives.TruffleBoundary(allowInlining = true)
    protected static int fastLookupBoundary(CLIType target, IType[] types) {
        return fastLookupImpl(target, types);
    }
}
