package com.vztekoverflow.bacil.runtime;

import com.vztekoverflow.bacil.BACILInternalError;
import sun.misc.Unsafe;

public class UnsafeHolder {
    private static final Unsafe UNSAFE;
    static {
        try {
            java.lang.reflect.Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (Unsafe) f.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new BACILInternalError("failed to get unsafe");
        }
    }

    public static Unsafe getUNSAFE() {
        return UNSAFE;
    }
}
