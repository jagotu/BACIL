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

    //private final EconomicMap<String, Object> loadedLibraries = EconomicMap.create();

    private static final InteropLibrary INTEROP = InteropLibrary.getFactory().getUncached();

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

    public CallTarget parseNativeLibrary(String path) throws UnsatisfiedLinkError {
        Object nativeLibrary = loadLibrary(path);
        if(nativeLibrary != null)
        {
            return (CallTarget) nativeLibrary;
        } else {
            throw new IllegalStateException("Native library call target is null.");
        }
    }

    private Object loadLibrary(String path) {
        return loadLibrary(path, false, null);
    }

    private Object loadLibrary(String path, boolean optional, String flags)
    {
        String loadExpression;
        if (flags == null) {
            loadExpression = String.format("load \"%s\"", path);
        } else {
            loadExpression = String.format("load(%s) \"%s\"", flags, path);
        }
        final Source source = Source.newBuilder("nfi", loadExpression, "(load " + path + ")").internal(true).build();
        try {
            // remove the call to the calltarget
            return env.parseInternal(source);
        } catch (UnsatisfiedLinkError ex) {
            if (optional) {
                return null;
            } else {
                throw ex;
            }
        }
    }
    public static Object getNativeFunctionOrNull(Object library, String name) {
        CompilerAsserts.neverPartOfCompilation();
        if (!INTEROP.isMemberReadable(library, name)) {
            // try another library
            return null;
        }
        try {
            return INTEROP.readMember(library, name);
        } catch (UnknownIdentifierException ex) {
            return null;
        } catch (InteropException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static Object bindNativeFunction(Object symbol, String signature) {
        try {
            return INTEROP.invokeMember(symbol, "bind", signature);
        } catch (InteropException ex) {
            throw new IllegalStateException(ex);
        }
    }


    public static InteropLibrary getINTEROP() {
        return INTEROP;
    }
}
