package com.vztekoverflow.cil.parser.bytecode;

/**
 * Marks evaluation stack slots where a primitive value is stored, specifying the type,
 * as defined in I.12.3.2.1 The evaluation stack.
 */
public class PrimitiveMarker {

    public static final byte EVALUATION_STACK_TAG_INT32 = 0;
    public static final byte EVALUATION_STACK_TAG_INT64 = 1;
    public static final byte EVALUATION_STACK_TAG_NATIVE_INT = 2;
    public static final byte EVALUATION_STACK_TAG_F = 3;

    public static final byte EVALUATION_STACK_TAG_MAX = 3;

    public static final PrimitiveMarker EVALUATION_STACK_INT32 = new PrimitiveMarker(EVALUATION_STACK_TAG_INT32);
    public static final PrimitiveMarker EVALUATION_STACK_INT64 = new PrimitiveMarker(EVALUATION_STACK_TAG_INT64);
    public static final PrimitiveMarker EVALUATION_STACK_NATIVE_INT = new PrimitiveMarker(EVALUATION_STACK_TAG_NATIVE_INT);
    public static final PrimitiveMarker EVALUATION_STACK_F = new PrimitiveMarker(EVALUATION_STACK_TAG_F);

    private final byte simpleTag;

    private PrimitiveMarker(byte simpleTag)
    {
        this.simpleTag = simpleTag;
    }

    public byte getTag() {
        return simpleTag;
    }

    public static boolean isEvaluationStackPrimitiveMarker(Object obj)
    {
        return obj == EVALUATION_STACK_INT32 || obj == EVALUATION_STACK_INT64 || obj == EVALUATION_STACK_F || obj == EVALUATION_STACK_NATIVE_INT;
    }
}
