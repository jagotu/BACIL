package com.vztekoverflow.bacil.runtime;

public class EvaluationStackPrimitiveMarker {

    public static final byte EVALUATION_STACK_TAG_INT32 = 0;
    public static final byte EVALUATION_STACK_TAG_INT64 = 1;
    public static final byte EVALUATION_STACK_TAG_NATIVE_INT = 2;
    public static final byte EVALUATION_STACK_TAG_F = 3;

    public static final byte EVALUATION_STACK_TAG_MAX = 3;

    public static final EvaluationStackPrimitiveMarker EVALUATION_STACK_INT32 = new EvaluationStackPrimitiveMarker(EVALUATION_STACK_TAG_INT32);
    public static final EvaluationStackPrimitiveMarker EVALUATION_STACK_INT64 = new EvaluationStackPrimitiveMarker(EVALUATION_STACK_TAG_INT64);
    public static final EvaluationStackPrimitiveMarker EVALUATION_STACK_NATIVE_INT = new EvaluationStackPrimitiveMarker(EVALUATION_STACK_TAG_NATIVE_INT);
    public static final EvaluationStackPrimitiveMarker EVALUATION_STACK_F = new EvaluationStackPrimitiveMarker(EVALUATION_STACK_TAG_F);

    private final byte simpleTag;

    private EvaluationStackPrimitiveMarker(byte simpleTag)
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
