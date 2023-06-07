package com.vztekoverflow.cilostazol;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.instrumentation.AllocationReporter;
import com.oracle.truffle.api.nodes.Node;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.GuestAllocator;

/**
 * The BACIL language class implementing TruffleLanguage.
 */
@TruffleLanguage.Registration(id = CILOSTAZOLLanguage.ID, name = CILOSTAZOLLanguage.NAME,
        contextPolicy = TruffleLanguage.ContextPolicy.SHARED, interactive = false,
        defaultMimeType = CILOSTAZOLLanguage.CIL_PE_MIME_TYPE,
        byteMimeTypes = {CILOSTAZOLLanguage.CIL_PE_MIME_TYPE})
public class CILOSTAZOLLanguage extends TruffleLanguage<CILOSTAZOLContext> {

    @CompilerDirectives.CompilationFinal
    private GuestAllocator allocator;

    @CompilerDirectives.CompilationFinal
    private final Assumption noAllocationTracking = Assumption.create("No allocation tracking assumption");

    public static final String ID = "cil";
    public static final String NAME = "CIL";

    public static final String CIL_PE_MIME_TYPE = "application/x-dosexec";

    private static final LanguageReference<CILOSTAZOLLanguage> REFERENCE = LanguageReference.create(CILOSTAZOLLanguage.class);

    public static CILOSTAZOLLanguage get(Node node) {
        return REFERENCE.get(node);
    }

    @Override
    protected CILOSTAZOLContext createContext(Env env) {
        return new CILOSTAZOLContext(this, env);
    }

    public boolean isAllocationTrackingDisabled() {
        return noAllocationTracking.isValid();
    }

    public void invalidateAllocationTrackingDisabled() {
        noAllocationTracking.invalidate();
    }

    public GuestAllocator getAllocator() {
        return allocator;
    }

    public void initializeGuestAllocator(TruffleLanguage.Env env) {
        this.allocator = new GuestAllocator(this, env.lookup(AllocationReporter.class));
    }
}
