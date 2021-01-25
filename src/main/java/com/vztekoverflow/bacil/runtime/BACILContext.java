package com.vztekoverflow.bacil.runtime;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.source.Source;
import com.vztekoverflow.bacil.BACILLanguage;
import org.graalvm.collections.EconomicMap;

import java.util.ArrayList;

public class BACILContext {
    private final BACILLanguage language;
    private final TruffleLanguage.Env env;

    public BACILLanguage getLanguage() {
        return language;
    }

    public TruffleLanguage.Env getEnv() {
        return env;
    }

    public BACILContext(BACILLanguage language, TruffleLanguage.Env env) {
        this.language = language;
        this.env = env;
    }

}
