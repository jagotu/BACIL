package com.vztekoverflow.bacil.interop;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

;

@ExportLibrary(InteropLibrary.class)
public final class ConstStringArray implements TruffleObject {
    @CompilerDirectives.CompilationFinal(dimensions = 1)
    final String[] array;

    public ConstStringArray(String[] array) {
        this.array = array;
    }
    @SuppressWarnings("static-method")
    @ExportMessage
    boolean hasArrayElements() {
        return true;
    }

    @ExportMessage
    Object readArrayElement(long index) throws InvalidArrayIndexException {
        if (!isArrayElementReadable(index)) {
            throw InvalidArrayIndexException.create(index);
        }
        return array[(int) index];
    }

    @ExportMessage
    long getArraySize() {
        return array.length;
    }

    @ExportMessage
    boolean isArrayElementReadable(long index) {
        return index >= 0L && index < array.length;
    }
}
