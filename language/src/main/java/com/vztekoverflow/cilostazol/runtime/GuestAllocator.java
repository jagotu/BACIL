package com.vztekoverflow.cilostazol.runtime;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.instrumentation.AllocationReporter;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.vztekoverflow.cilostazol.CILOSTAZOLLanguage;
import com.vztekoverflow.cilostazol.context.ContextAccessImpl;
import com.vztekoverflow.cilostazol.context.LanguageAccess;
import com.vztekoverflow.cilostazol.exceptions.InstantiationError;
import com.vztekoverflow.cilostazol.exceptions.InstantiationException;
import com.vztekoverflow.cilostazol.exceptions.NegativeArraySizeException;
import com.vztekoverflow.cilostazol.meta.SystemTypes;
import com.vztekoverflow.cilostazol.runtime.objectmodel.StaticObject;
import com.vztekoverflow.cilostazol.runtime.typesystem.field.IField;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.CLIType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.TypeBase;

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

    /**
     * Allocates a new instance of the given class; does not call any constructor. Initializes the
     * class.
     *
     * @param type The type of the reference to allocate. If it is PE-constant, the field
     *             initialization loop can be exploded. This is expected to be the case when
     *             executing the {@code NEW} bytecode, but may not be the case always.
     */
    public StaticObject createNew(TypeBase<?> type) {
        assert AllocationChecks.canAllocateNewReference(type);
        type.safelyInitialize();
        StaticObject newObj = type.getShape(false).getFactory().create(type);
        initInstanceFields(newObj, type);
        return trackAllocation(type, newObj);
    }

    // TODO: This might take constructors into consideration
    public StaticObject createClass(TypeBase<?> type) {
        return createNew(type);
    }

    private static void initInstanceFields(StaticObject obj, TypeBase<?> thisKlass) {
        if (CompilerDirectives.isPartialEvaluationConstant(thisKlass)) {
            initLoop(obj, thisKlass);
        } else {
            initLoopNoExplode(obj, thisKlass);
        }
    }

    @ExplodeLoop
    private static void initLoop(StaticObject obj, TypeBase<?> type) {
        for (IField f : type.getFields()) {
            assert !f.isStatic();
            if (f.getKind() == SystemTypes.Object) {
                f.setObjectValue(obj, StaticObject.NULL);
            }
        }
    }

    private static void initLoopNoExplode(StaticObject obj, TypeBase<?> type) {
        for (IField f : type.getFields()) {
            assert !f.isStatic();
            if (f.getKind() == SystemTypes.Object) {
                f.setObjectValue(obj, StaticObject.NULL);
            }
        }
    }

    private StaticObject trackAllocation(TypeBase<?> type, StaticObject obj) {
        return trackAllocation(type, obj, getLanguage(), type);
    }

    private static StaticObject trackAllocation(TypeBase<?> type, StaticObject obj, CILOSTAZOLLanguage lang, ContextAccessImpl contextAccess) {
        if (type == null || lang.isAllocationTrackingDisabled()) {
            return obj;
        }
        if (!CompilerDirectives.isPartialEvaluationConstant(contextAccess)) {
            return trackAllocationBoundary(contextAccess, obj);
        }
        return contextAccess.getAllocator().trackAllocation(obj);
    }

    @CompilerDirectives.TruffleBoundary
    private static StaticObject trackAllocationBoundary(ContextAccessImpl context, StaticObject obj) {
        return context.getAllocator().trackAllocation(obj);
    }

    public <T> T trackAllocation(T object) {
        if (allocationReporter != null) {
            CompilerAsserts.partialEvaluationConstant(allocationReporter);
            allocationReporter.onEnter(null, 0, AllocationReporter.SIZE_UNKNOWN);
            allocationReporter.onReturnValue(object, 0, AllocationReporter.SIZE_UNKNOWN);
        }
        return object;
    }

    public interface AllocationProfiler {
        AllocationProfiler NO_PROFILE = new AllocationProfiler() {
            @Override
            public void enterNewReference() {

            }

            @Override
            public void enterNewArray() {

            }

            @Override
            public void enterNewMultiArray() {

            }
        };

        void enterNewReference();

        void enterNewArray();

        void enterNewMultiArray();
    }

    public static final class AllocationChecks {
        private AllocationChecks() {
        }

        public static void checkCanAllocateNewReference(CLIType type, boolean error) {
            checkCanAllocateNewReference(type, error, AllocationProfiler.NO_PROFILE);
        }

        public static void checkCanAllocateArray(int size) {
            checkCanAllocateArray(size, AllocationProfiler.NO_PROFILE);
        }

        public static void checkCanAllocateNewReference(CLIType type, boolean error, AllocationProfiler profile) {
            if (!canAllocateNewReference(type)) {
                profile.enterNewReference();
                throw error ? new InstantiationError() : new InstantiationException();
            }
        }

        public static void checkCanAllocateArray(int size, AllocationProfiler profile) {
            if (!canAllocateNewArray(size)) {
                profile.enterNewArray();
                throw new NegativeArraySizeException();
            }
        }

        private static boolean canAllocateNewReference(CLIType type) {
            return (type instanceof TypeBase<?>) && !type.isAbstract() && !type.isInterface();
        }

        private static boolean canAllocateNewArray(int size) {
            return size >= 0;
        }

        private static boolean invalidDimensionsArray(int[] dimensions) {
            return dimensions.length == 0 || dimensions.length > 255;
        }

        @ExplodeLoop
        private static boolean invalidDimensions(int[] dimensions) {
            if (CompilerDirectives.isPartialEvaluationConstant(dimensions)) {
                return invalidDimensionsExplode(dimensions);
            } else {
                return invalidDimensionsNoExplode(dimensions);
            }
        }

        private static boolean invalidDimensionsNoExplode(int[] dimensions) {
            for (int dim : dimensions) {
                if (!canAllocateNewArray(dim)) {
                    return true;
                }
            }
            return false;
        }

        @ExplodeLoop
        private static boolean invalidDimensionsExplode(int[] dimensions) {
            for (int dim : dimensions) {
                if (!canAllocateNewArray(dim)) {
                    return true;
                }
            }
            return false;
        }
    }
}