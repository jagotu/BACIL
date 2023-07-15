package com.vztekoverflow.cilostazol;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.instrumentation.AllocationReporter;
import com.oracle.truffle.api.nodes.Node;
import com.vztekoverflow.cilostazol.nodes.CallEntryPointCallTarget;
import com.vztekoverflow.cilostazol.runtime.context.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.other.GuestAllocator;
import com.vztekoverflow.cilostazol.runtime.symbols.MethodSymbol;
import org.graalvm.options.OptionDescriptors;
import org.graalvm.polyglot.Source;

/** The BACIL language class implementing TruffleLanguage. */
@TruffleLanguage.Registration(
    id = CILOSTAZOLLanguage.ID,
    name = CILOSTAZOLLanguage.NAME,
    interactive = false,
    defaultMimeType = CILOSTAZOLLanguage.CIL_PE_MIME_TYPE,
    byteMimeTypes = {CILOSTAZOLLanguage.CIL_PE_MIME_TYPE})
public class CILOSTAZOLLanguage extends TruffleLanguage<CILOSTAZOLContext> {

  public static final String ID = "cil";
  public static final String NAME = "CIL";
  public static final String CIL_PE_MIME_TYPE = "application/x-dosexec";
  private static final LanguageReference<CILOSTAZOLLanguage> REFERENCE =
      LanguageReference.create(CILOSTAZOLLanguage.class);

  @CompilerDirectives.CompilationFinal
  private final Assumption noAllocationTracking =
      Assumption.create("No allocation tracking assumption");

  @CompilerDirectives.CompilationFinal private GuestAllocator allocator;

  public static CILOSTAZOLLanguage get(Node node) {
    return REFERENCE.get(node);
  }

  @Override
  protected OptionDescriptors getOptionDescriptors() {
    return new CILOSTAZOLEngineOptionOptionDescriptors();
  }

  @Override
  protected CILOSTAZOLContext createContext(Env env) {
    return new CILOSTAZOLContext(this, env);
  }

  @Override
  protected CallTarget parse(ParsingRequest request) throws Exception {
    var source =
        Source.newBuilder(
                CILOSTAZOLLanguage.ID,
                request.getSource().getBytes(),
                request.getSource().getName())
            .build();

    var assembly = CILOSTAZOLContext.get(null).loadAssembly(source);
    MethodSymbol main = assembly.getEntryPoint();
    return new CallEntryPointCallTarget(
        main.getNode().getCallTarget(), main.getParameters().length == 1);
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
