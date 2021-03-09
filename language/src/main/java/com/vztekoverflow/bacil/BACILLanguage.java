package com.vztekoverflow.bacil;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.source.Source;
import com.vztekoverflow.bacil.parser.cil.CILMethod;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.runtime.BACILContext;
import org.graalvm.options.OptionDescriptors;

import java.io.File;

@TruffleLanguage.Registration(id = BACILLanguage.ID, name = BACILLanguage.NAME, interactive = false, defaultMimeType = BACILLanguage.CIL_PE_MIME_TYPE,
byteMimeTypes = {BACILLanguage.CIL_PE_MIME_TYPE})
public class BACILLanguage extends TruffleLanguage<BACILContext> {

    public static final String ID = "cil";
    public static final String NAME = "CIL";

    public static final String CIL_PE_MIME_TYPE = "application/x-dosexec";

    public BACILLanguage()
    {
        int a = 0;
    }

    @Override
    protected BACILContext createContext(Env env) {
        return new BACILContext(this, env);
    }

    private static class AddCLIArgsCallTarget implements CallTarget
    {

        private final CallTarget inner;

        private AddCLIArgsCallTarget(CallTarget inner) {
            this.inner = inner;
        }

        @Override
        public Object call(Object... arguments) {
            assert arguments.length == 0;
            return inner.call((Object) null);
        }
    }

    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        Source source = request.getSource();

        CLIComponent c = getCurrentContext(BACILLanguage.class).loadAssembly(source, this);

        String sourcePath = request.getSource().getPath();
        File file = new File(sourcePath);
        getCurrentContext(BACILLanguage.class).addLibraryPath(file.getAbsoluteFile().getParent());
        //DebugNode node = new DebugNode(this, new FrameDescriptor(), c);
        if(c.getCliHeader().getEntryPointToken() == 0)
        {
            throw new RuntimeException("No entry point in file");
        }

        CLITablePtr entryPtr = CLITablePtr.fromToken(c.getCliHeader().getEntryPointToken());

        CILMethod entryMethod = c.getLocalMethod(entryPtr);
        return new AddCLIArgsCallTarget(entryMethod.getMethodCallTarget());
    }

    @Override
    protected OptionDescriptors getOptionDescriptors() {
        return new BACILEngineOptionOptionDescriptors();
    }
    
}
