package com.vztekoverflow.cilostazol.nodes;

import com.oracle.truffle.api.HostCompilerDirectives;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.BytecodeOSRNode;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.vztekoverflow.cilostazol.exceptions.NotImplementedException;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.IMethod;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;

public class CILMethodNode extends CILNodeBase implements BytecodeOSRNode {
    private final IMethod _method;
    private final byte[] _cil;
    private final FrameDescriptor _frameDescriptor;
    private final CILOSTAZOLFrame.CILOSTAZOLFrameSlotKind[] _taggedFrame;

    private CILMethodNode(IMethod method, byte[] cilCode) {
        _method = method;
        _cil = cilCode;
        _frameDescriptor = CILOSTAZOLFrame.create(method.getLocals().length, method.getMaxStack());
        _taggedFrame = new CILOSTAZOLFrame.CILOSTAZOLFrameSlotKind[_frameDescriptor.getNumberOfSlots()];
    }

    public static CILMethodNode create(IMethod method, byte[] cilCode) {
        return new CILMethodNode(method, cilCode);
    }

    public IMethod getMethod() {
        return _method;
    }

    public FrameDescriptor getFrameDescriptor() {
        return _frameDescriptor;
    }

    //region CILNodeBase
    @Override
    public Object execute(VirtualFrame frame) {
        initializeFrame(frame);
        return execute(frame, 0, CILOSTAZOLFrame.getStartStackOffset(_method));
    }

    private void initializeFrame(VirtualFrame frame) {
        frame.isInt(1);
        Object[] args = frame.getArguments();
        IType[] argTypes = _method.getParameters();
        int topStack = CILOSTAZOLFrame.getStartStackOffset(_method) - 1;

        for(int i = 0; i < _method.getParameters().length; i++) {
            switch(CILOSTAZOLFrame.getKind(argTypes[i])) {
                case Object:
                    CILOSTAZOLFrame.putObject(frame, topStack, args[i]);
                    break;
                case Boolean:
                    CILOSTAZOLFrame.putBoolean(frame, topStack, (boolean) args[i]);
                    break;
                case Byte:
                    CILOSTAZOLFrame.putByte(frame, topStack, (byte)args[i]);
                    break;
                case Int:
                    CILOSTAZOLFrame.putInt(frame, topStack, (int)args[i]);
                    break;
                case Long:
                    CILOSTAZOLFrame.putLong(frame, topStack, (int)args[i]);
                    break;
                case Double:
                    CILOSTAZOLFrame.putDouble(frame, topStack, (double)args[i]);
                    break;
                case Float:
                    CILOSTAZOLFrame.putFloat(frame, topStack, (float) args[i]);
                    break;
            }
            _taggedFrame[topStack] = CILOSTAZOLFrame.getKind(argTypes[i]);
            topStack--;
        }
    }
    //endregion

    //region OSR
    @Override
    public Object executeOSR(VirtualFrame osrFrame, int target, Object interpreterState) {
        throw new NotImplementedException();
    }

    @Override
    public Object getOSRMetadata() {
        throw new NotImplementedException();
    }

    @Override
    public void setOSRMetadata(Object osrMetadata) {
        throw new NotImplementedException();
    }
    //endregion

    @ExplodeLoop(kind = ExplodeLoop.LoopExplosionKind.MERGE_EXPLODE)
    @HostCompilerDirectives.BytecodeInterpreterSwitch
    private Object execute(VirtualFrame frame, int pc, int topStack) {
        throw new NotImplementedException();
    }
}
