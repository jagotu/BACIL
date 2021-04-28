package com.vztekoverflow.bacil.parser.cil;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.nodes.BytecodeNode;
import com.vztekoverflow.bacil.nodes.CILRootNode;
import com.vztekoverflow.bacil.parser.BACILParserException;
import com.vztekoverflow.bacil.parser.ByteSequenceBuffer;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.CLITablePtr;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLIMethodDefTableRow;
import com.vztekoverflow.bacil.parser.signatures.LocalVarSig;
import com.vztekoverflow.bacil.parser.signatures.MethodDefSig;
import com.vztekoverflow.bacil.runtime.BACILMethod;
import com.vztekoverflow.bacil.runtime.types.ByRefWrapped;
import com.vztekoverflow.bacil.runtime.types.Type;
import com.vztekoverflow.bacil.runtime.types.builtin.SystemValueTypeType;
import com.vztekoverflow.bacil.runtime.types.locations.LocationsDescriptor;

public class CILMethod implements BACILMethod {

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



    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private final Type[] locationTypes;

    private final LocationsDescriptor locationDescriptor;

    private final int varsCount;
    private final int argsCount;

    private final Type definingType;

    public CILMethod(CLIComponent component, CLIMethodDefTableRow methodDef, Type definingType)
    {
        this.component = component;
        this.methodDef = methodDef;
        this.methodDefSig = MethodDefSig.read(methodDef.getSignature().read(component.getBlobHeap()), component);
        this.definingType = definingType;

        ByteSequenceBuffer buf = component.getBuffer(methodDef.getRVA());
        byte firstByte = buf.getByte();

        final int size;

        if((firstByte & 3) == CORILMETHOD_TINYFORMAT)
        {
            this.flags = CORILMETHOD_TINYFORMAT;
            this.maxStack = 8;
            this.localVarSig = null;
            this.initLocals = false;
            size = (firstByte >> 2)&0xFF;
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
                this.localVarSig = LocalVarSig.read(localVarSig, component);
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


        int explicitArgsStart = 0;
        if (methodDefSig.isHasThis() && !methodDefSig.isExplicitThis())
        {
            if(definingType == null)
            {
                throw new BACILInternalError("Instance method constructor called without definingType!");
            }
            explicitArgsStart = 1;
        }

        argsCount = methodDefSig.getParamTypes().length + explicitArgsStart;

        if(localVarSig != null)
        {
            varsCount = localVarSig.getVarTypes().length;
        } else {
            varsCount = 0;
        }

        locationTypes = new Type[varsCount+argsCount];

        for(int i = 0; i < varsCount; i++)
        {
            locationTypes[i] = localVarSig.getVarTypes()[i];
        }

        if(methodDefSig.isHasThis() && !methodDefSig.isExplicitThis())
        {
            //I.8.6.1.5
            if(definingType instanceof SystemValueTypeType)
            {
                //TODO if virtual then boxed
                locationTypes[varsCount] = new ByRefWrapped(definingType);
            } else {
                locationTypes[varsCount] = definingType;
            }

        }


        for(int i = explicitArgsStart; i < argsCount; i++)
        {
            locationTypes[varsCount+i] = methodDefSig.getParamTypes()[i-explicitArgsStart];
        }

        this.locationDescriptor = new LocationsDescriptor(locationTypes);
    }

    public CLIComponent getComponent() {
        return component;
    }

    @Override
    public CallTarget getMethodCallTarget() {
        return callTarget;
    }

    @Override
    public Type getRetType() {
        return getMethodDefSig().getRetType();
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

    @Override
    public String getName() {
        return name;
    }

    public boolean isInitLocals() {
        return initLocals;
    }


    @Override
    public Type[] getLocationsTypes() {
        return locationTypes;
    }

    @Override
    public int getVarsCount() {
        return varsCount;
    }

    @Override
    public int getArgsCount() {
        return argsCount;
    }

    @Override
    public String toString() {
        return definingType.toString() + "::" + getName();
    }

    @Override
    public Type getDefiningType() {
        return definingType;
    }

    public LocationsDescriptor getLocationDescriptor() {
        return locationDescriptor;
    }
}
