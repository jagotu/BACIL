package com.vztekoverflow.bacil.runtime;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

@ExportLibrary(InteropLibrary.class)
public class NativePointer implements TruffleObject {

    private final long pointerValue;
    public static final NativePointer NULL = new NativePointer(0);

    public NativePointer(long pointerValue) {
        this.pointerValue = pointerValue;
    }

    @ExportMessage
    public boolean isPointer()
    {
        return true;
    }

    @ExportMessage
    public long asPointer()
    {
        return pointerValue;
    }

    public int getInt()
    {
        return UnsafeWrapper.UNSAFE.getInt(pointerValue);
    }

    public String getUTF16_NT()
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 0;; i++)
        {
            char c = (char) UnsafeWrapper.UNSAFE.getShort(pointerValue + i*2);
            if(c == 0)
            {
                break;
            }
            sb.append(c);
        }
        return sb.toString();
    }


}
