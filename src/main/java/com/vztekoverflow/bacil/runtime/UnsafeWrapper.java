package com.vztekoverflow.bacil.runtime;

import com.oracle.truffle.api.object.DynamicObject;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeWrapper {


    public static final Unsafe UNSAFE;

    static {
        UNSAFE = getUnsafe();
    }

    private static Unsafe getUnsafe() {
        try {
            return Unsafe.getUnsafe();
        } catch (SecurityException e) {
        }
        try {
            Field theUnsafeInstance = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafeInstance.setAccessible(true);
            return (Unsafe) theUnsafeInstance.get(Unsafe.class);
        } catch (Exception e) {
            throw new RuntimeException("exception while trying to get Unsafe.theUnsafe via reflection:", e);
        }
    }

    public static NativePointer nativeInt()
    {
        return nativeInt(0);
    }

    public static NativePointer nativeInt(int init)
    {
        long ptr = UNSAFE.allocateMemory(4);
        UNSAFE.putInt(ptr, init);
        return new NativePointer(ptr);
    }

    public static NativePointer nativeAlloc(long bytes)
    {
        return new NativePointer(UNSAFE.allocateMemory(bytes));
    }
}
