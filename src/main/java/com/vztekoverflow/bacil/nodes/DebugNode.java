package com.vztekoverflow.bacil.nodes;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.RootNode;
import com.vztekoverflow.bacil.BACILLanguage;
import com.vztekoverflow.bacil.parser.cli.CLIComponent;
import com.vztekoverflow.bacil.parser.cli.tables.generated.CLIMethodDefTableRow;
import com.vztekoverflow.bacil.runtime.BACILContext;
import com.vztekoverflow.bacil.runtime.NativePointer;
import com.vztekoverflow.bacil.runtime.UnsafeWrapper;
import sun.misc.Unsafe;

import java.lang.annotation.Native;

@NodeInfo(language = "CLI", description = "Prints some random stuff about the  CLIComponent")
public class DebugNode extends RootNode {

    private final CLIComponent component;

    public DebugNode(TruffleLanguage<?> language, FrameDescriptor frameDescriptor, CLIComponent component) {
        super(language, frameDescriptor);
        this.component = component;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        for(CLIMethodDefTableRow methodRow : component.getTableHeads().getMethodDefTableHead())
        {
            System.out.println(methodRow.getName().read(component.getStringHeap()));
        }

        BACILContext context = lookupContextReference(BACILLanguage.class).get();
        CallTarget target = context.parseNativeLibrary("kernel32.dll");
        Object library = target.call();

        if(library != null)
        {
            Object fun = BACILContext.getNativeFunctionOrNull(library, "GetComputerNameW");
            if(fun != null)
            {
                Object bound = BACILContext.bindNativeFunction(fun, "(POINTER,POINTER):SINT32");

                try {
                    NativePointer nSize = UnsafeWrapper.nativeInt();

                    BACILContext.getINTEROP().execute(bound, NativePointer.NULL, nSize);

                    NativePointer strBuffer = UnsafeWrapper.nativeAlloc(nSize.getInt()* 2L);

                    BACILContext.getINTEROP().execute(bound, strBuffer, nSize);

                    System.out.println(strBuffer.getUTF16_NT());


                } catch (UnsupportedTypeException | UnsupportedMessageException | ArityException e) {
                    e.printStackTrace();
                }


            }
        }

        return "lol";
    }


}
