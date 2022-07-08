package com.vztekoverflow.bacil.interop;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.RootNode;
import com.vztekoverflow.bacil.runtime.BACILContext;

public class GetBindingsNode extends RootNode {
    public static final String EVAL_NAME = "<Bindings>";

    public final TruffleObject bindings;

    public GetBindingsNode(TruffleLanguage<?> language, BACILContext context) {
        super(language);
        bindings = context.getBindings();
    }

    @Override
    public Object execute(VirtualFrame frame) {
         return bindings;
    }
}
