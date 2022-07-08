package com.vztekoverflow.bacil;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.Source;
import com.vztekoverflow.bacil.interop.GetBindingsNode;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.runtime.BACILContext;
import com.vztekoverflow.bacil.runtime.BACILMethod;
import com.vztekoverflow.bacil.runtime.SZArray;
import com.vztekoverflow.bacil.runtime.types.builtin.BuiltinTypes;
import org.graalvm.options.OptionDescriptors;

import java.io.File;

/**
 * The BACIL language class implementing TruffleLanguage.
 */
@TruffleLanguage.Registration(id = BACILLanguage.ID, name = BACILLanguage.NAME, defaultMimeType = BACILLanguage.BACIL_MIME_TYPE,
byteMimeTypes = {BACILLanguage.CIL_PE_MIME_TYPE}, characterMimeTypes = {BACILLanguage.BACIL_MIME_TYPE})
public class BACILLanguage extends TruffleLanguage<BACILContext> {

    public static final String ID = "cil";
    public static final String NAME = "CIL";

    public static final String CIL_PE_MIME_TYPE = "application/x-dosexec";
    public static final String BACIL_MIME_TYPE = "application/x-bacil";

    public BACILLanguage()
    {
        int a = 0;
    }

    @Override
    protected BACILContext createContext(Env env) {
        return new BACILContext(this, env);
    }

    /**
     * CallTarget used to call the entry point, adding command line arguments and returning 0 from voids.
     */
    private static class CallEntryPointCallNode extends RootNode
    {

        private final CallTarget inner;
        private final boolean shouldAddArgs;
        private final Object arg;

        private CallEntryPointCallNode(BACILLanguage language, CallTarget inner, boolean shouldAddArgs, BuiltinTypes builtinTypes) {
            super(language);
            this.inner = inner;
            this.shouldAddArgs = shouldAddArgs;
            if(shouldAddArgs)
            {
                SZArray argsArray = new SZArray(builtinTypes.getStringType(), 0);
                arg = argsArray;
            } else {
                arg = null;
            }
        }

        @Override
        public Object execute(VirtualFrame frame) {

            Object result;
            if(shouldAddArgs)
            {
                result = inner.call(arg);
            } else {
                result = inner.call();
            }


            if(result == null)
                return 0;

            return result;
        }
    }

    @Override
    protected CallTarget parse(ParsingRequest request) {
        Source source = request.getSource();

        if(source.hasCharacters())
        {
            String contents = request.getSource().getCharacters().toString();
            if (GetBindingsNode.EVAL_NAME.equals(contents)) {
                RootNode node = new GetBindingsNode(this, getCurrentContext(BACILLanguage.class));
                return node.getCallTarget();
            } else {
                throw new UnsupportedOperationException("The evaluated expression is not a known BACIL command.");
            }
        }

        CLIComponent c = getCurrentContext(BACILLanguage.class).loadAssembly(source);

        String sourcePath = request.getSource().getPath();
        File file = new File(sourcePath);
        getCurrentContext(BACILLanguage.class).addLibraryPath(file.getAbsoluteFile().getParent());

        if(c.getCliHeader().getEntryPointToken() == 0)
        {
            throw new RuntimeException("No entry point in file");
        }

        CLITablePtr entryPtr = CLITablePtr.fromToken(c.getCliHeader().getEntryPointToken());

        BACILMethod entryMethod = c.getLocalMethod(entryPtr);
        return new CallEntryPointCallNode(this, entryMethod.getMethodCallTarget(), entryMethod.getArgsCount() == 1, c.getBuiltinTypes()).getCallTarget();
    }

    @Override
    protected Object getScope(BACILContext context) {
        return context.getBindings();
    }

    @Override
    protected OptionDescriptors getOptionDescriptors() {
        return new BACILEngineOptionOptionDescriptors();
    }
    
}
