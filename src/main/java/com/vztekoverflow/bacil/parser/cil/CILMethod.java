package com.vztekoverflow.bacil.parser.cil;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.vztekoverflow.bacil.nodes.BytecodeNode;
import com.vztekoverflow.bacil.nodes.CILRootNode;
import com.vztekoverflow.bacil.parser.BACILParserException;
import com.vztekoverflow.bacil.parser.ByteSequenceBuffer;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLIMethodDefTableRow;

public class CILMethod {

    private final CLIComponent component;
    private final CLIMethodDefTableRow methodDef;
    private final int flags;
    private final short maxStack;
    private final int localVarSigTok;
    private final CallTarget callTarget;

    private static final byte CORILMETHOD_TINYFORMAT = 2;
    private static final byte CORILMETHOD_FATFORMAT = 3;

    public CILMethod(CLIComponent component, CLIMethodDefTableRow methodDef)
    {
        this.component = component;
        this.methodDef = methodDef;

        ByteSequenceBuffer buf = component.getBuffer(methodDef.getRVA());
        byte firstByte = buf.getByte();

        final int size;

        if((firstByte & 3) == CORILMETHOD_TINYFORMAT)
        {
            this.flags = CORILMETHOD_TINYFORMAT;
            this.maxStack = 8;
            this.localVarSigTok = 0;
            size = firstByte >> 2;
        } else if((firstByte & 3) == CORILMETHOD_FATFORMAT) {
            short firstWord = (short)(firstByte | (buf.getByte() << 8));
            this.flags = firstWord & 0xFFF;
            byte headerSize = (byte)(firstWord >> 12);

            if(headerSize != 3)
            {
                throw new BACILParserException("Unexpected CorILMethod fat header size");
            }

            this.maxStack = buf.getShort();
            size = buf.getInt();
            localVarSigTok = buf.getInt();

        } else {
            throw new BACILParserException("Invalid CorILMethod flags");
        }

        final byte[] body = buf.subSequence(size).toByteArray();
        //TODO other method types
        FrameDescriptor frameDescriptor = new FrameDescriptor();
        CILRootNode rootNode = new CILRootNode(frameDescriptor, new BytecodeNode(this, body));
        this.callTarget = Truffle.getRuntime().createCallTarget(rootNode);
    }

    public CLIComponent getComponent() {
        return component;
    }

    public CallTarget getCallTarget() {
        return callTarget;
    }
}
