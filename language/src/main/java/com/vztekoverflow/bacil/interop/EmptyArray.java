package com.vztekoverflow.bacil.interop;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

@ExportLibrary(InteropLibrary.class)
public final class EmptyArray implements TruffleObject {
    public static final EmptyArray INSTANCE = new EmptyArray();

    @ExportMessage
    @SuppressWarnings("static-method")
    boolean hasArrayElements() {
        return true;
    }

    @ExportMessage
    @SuppressWarnings("static-method")
    long getArraySize() {
        return 0;
    }

    @ExportMessage
    @SuppressWarnings("static-method")
    boolean isArrayElementReadable(@SuppressWarnings("unused") long index) {
        return false;
    }

    @ExportMessage
    @SuppressWarnings("static-method")
    Object readArrayElement(long index) throws InvalidArrayIndexException {
        throw InvalidArrayIndexException.create(index);
    }
}