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
import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLIMethodDefTableRow;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLITableHeads;
import com.vztekoverflow.bacil.parser.signatures.LocalVarSig;
import com.vztekoverflow.bacil.parser.signatures.MethodDefSig;

public class CILMethod {

    private final CLIComponent component;
    private final CLIMethodDefTableRow methodDef;
    private final int flags;
    private final short maxStack;

    private final CallTarget callTarget;

    private final LocalVarSig localVarSig;
    private final MethodDefSig methodDefSig;
    private final String name;

    private final boolean initLocals;

    private static final byte CORILMETHOD_TINYFORMAT = 2;
    private static final byte CORILMETHOD_FATFORMAT = 3;
    private static final byte CORILMETHOD_INITLOCALS = 0x10;
    private static final byte CORILMETHOD_MORESECTS = 0x8;

    public CILMethod(CLIComponent component, CLIMethodDefTableRow methodDef)
    {
        this.component = component;
        this.methodDef = methodDef;
        this.methodDefSig = MethodDefSig.read(methodDef.getSignature().read(component.getBlobHeap()));

        ByteSequenceBuffer buf = component.getBuffer(methodDef.getRVA());
        byte firstByte = buf.getByte();

        final int size;

        if((firstByte & 3) == CORILMETHOD_TINYFORMAT)
        {
            this.flags = CORILMETHOD_TINYFORMAT;
            this.maxStack = 8;
            this.localVarSig = null;
            this.initLocals = false;
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

            int localVarSigTok = buf.getInt();
            if(localVarSigTok == 0)
            {
                this.localVarSig = null;
            } else {
                CLITablePtr localVarSigPtr = CLITablePtr.fromToken(localVarSigTok);
                byte[] localVarSig = component.getTableHeads().getStandAloneSigTableHead().skip(localVarSigPtr).getSignature().read(component.getBlobHeap());
                this.localVarSig = LocalVarSig.read(localVarSig);
            }

            initLocals = (flags & CORILMETHOD_INITLOCALS) != 0;
            if((flags & CORILMETHOD_MORESECTS) != 0)
            {
                throw new BACILParserException("Multiple sections in CIL method header not supported.");
            }

        } else {
            throw new BACILParserException("Invalid CorILMethod flags");
        }

        this.name = methodDef.getName().read(component.getStringHeap());

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

    public short getMaxStack() {
        return maxStack;
    }

    public CLIMethodDefTableRow getMethodDef() {
        return methodDef;
    }

    public int getFlags() {
        return flags;
    }

    public LocalVarSig getLocalVarSig() {
        return localVarSig;
    }

    public MethodDefSig getMethodDefSig() {
        return methodDefSig;
    }

    public String getName() {
        return name;
    }

    public boolean isInitLocals() {
        return initLocals;
    }


}
