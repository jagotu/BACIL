package com.vztekoverflow.bacil.runtime.bacil;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.nodes.RootNode;
import com.vztekoverflow.bacil.runtime.BACILMethod;

public abstract class JavaMethod extends RootNode implements BACILMethod {

    protected JavaMethod(TruffleLanguage<?> language) {
        super(language);
    }

    protected JavaMethod(TruffleLanguage<?> language, FrameDescriptor frameDescriptor) {
        super(language, frameDescriptor);
    }

}
