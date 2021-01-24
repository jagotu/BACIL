package com.vztekoverflow.bacil;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.source.Source;
import com.vztekoverflow.bacil.parser.BACILParserException;
import com.vztekoverflow.bacil.parser.cil.CILMethod;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLIMethodDefTableRow;
import com.vztekoverflow.bacil.runtime.BACILContext;
import org.graalvm.polyglot.io.ByteSequence;

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
        ByteSequence bytes;

        if (source.hasBytes()) {
            bytes = source.getBytes();
        } else if (source.hasCharacters()) {
            throw new BACILParserException("Unexpected character-based source with mime type: " + source.getMimeType());
        } else {
            throw new BACILParserException("Should not reach here: Source is neither char-based nor byte-based!");
        }

        CLIComponent c = CLIComponent.parseComponent(bytes, source, this);
        //DebugNode node = new DebugNode(this, new FrameDescriptor(), c);
        if(c.getCliHeader().getEntryPointToken() == 0)
        {
            throw new RuntimeException("No entry point in file");
        }

        CLITablePtr entryPtr = CLITablePtr.fromToken(c.getCliHeader().getEntryPointToken());

        CILMethod entryMethod = c.getLocalMethod(entryPtr);
        return new AddCLIArgsCallTarget(entryMethod.getCallTarget());
    }
}
