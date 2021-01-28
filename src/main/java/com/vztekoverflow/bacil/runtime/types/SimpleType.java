package com.vztekoverflow.bacil.runtime.types;


import com.oracle.truffle.api.CompilerDirectives;
import com.vztekoverflow.bacil.BACILInternalError;
import com.vztekoverflow.bacil.runtime.ExecutionStackPrimitiveMarker;

//types that don't have any further variability
public class SimpleType extends Type {

    public static final SimpleType TYPEDBYREF = new SimpleType(Type.ELEMENT_TYPE_TYPEDBYREF);
    public static final SimpleType VOID = new SimpleType(Type.ELEMENT_TYPE_VOID);

    private final byte typeCategory;

    public SimpleType(byte typeCategory) {
        this.typeCategory = typeCategory;
    }

    @Override
    public byte getTypeCategory() {
        return typeCategory;
    }

    @Override
    public Object defaultConstructor() {
        switch(typeCategory)
        {
            case ELEMENT_TYPE_BOOLEAN:
            case ELEMENT_TYPE_U1:
            case ELEMENT_TYPE_I1:
                return (byte) 0;

            case ELEMENT_TYPE_CHAR:
            case ELEMENT_TYPE_U2:
            case ELEMENT_TYPE_I2:
                return (short) 0;

            case ELEMENT_TYPE_U4:
            case ELEMENT_TYPE_I4:
            case ELEMENT_TYPE_R4:
                return 0;

            case ELEMENT_TYPE_U8:
            case ELEMENT_TYPE_I8:
            case ELEMENT_TYPE_R8:
            case ELEMENT_TYPE_I:
            case ELEMENT_TYPE_U:
                return 0L;

            default:
                throw new BACILInternalError("Initializing simple type not implemented: type " + typeCategory);
        }
    }

    @Override
    public Object fromStackVar(Object ref, long primitive) {
        if(ref == ExecutionStackPrimitiveMarker.EXECUTION_STACK_INT32 || ref == ExecutionStackPrimitiveMarker.EXECUTION_STACK_INT64 || ref == ExecutionStackPrimitiveMarker.EXECUTION_STACK_NATIVE_INT )
        {
            long value = primitive;
            if(ref == ExecutionStackPrimitiveMarker.EXECUTION_STACK_INT32)
                value &= 0xFFFFFFFF;
            switch(typeCategory)
            {
                case ELEMENT_TYPE_BOOLEAN:
                case ELEMENT_TYPE_U1:
                case ELEMENT_TYPE_I1:
                    return (byte) value;

                case ELEMENT_TYPE_CHAR:
                case ELEMENT_TYPE_U2:
                case ELEMENT_TYPE_I2:
                    return (short) value;

                case ELEMENT_TYPE_U4:
                case ELEMENT_TYPE_I4:
                case ELEMENT_TYPE_R4:
                    return (int) value;

                case ELEMENT_TYPE_U8:
                case ELEMENT_TYPE_I8:
                case ELEMENT_TYPE_R8:
                case ELEMENT_TYPE_I:
                case ELEMENT_TYPE_U:
                    if(ref == ExecutionStackPrimitiveMarker.EXECUTION_STACK_INT32) {
                        CompilerDirectives.transferToInterpreterAndInvalidate();
                        throw new BACILInternalError("Attempting to store int32 stack slot to an int64 object slot");
                    }
                    return value;
            }

        }
        CompilerDirectives.transferToInterpreterAndInvalidate();
        throw new BACILInternalError("Unrecognized stack var type");
    }

    @Override
    public void toStackVar(Object[] refs, long[] primitives, int slot, Object value) {
        switch(typeCategory)
        {
            case ELEMENT_TYPE_BOOLEAN:
            case ELEMENT_TYPE_U1:
                primitives[slot] = ((Byte)value & 0xFF); refs[slot] = ExecutionStackPrimitiveMarker.EXECUTION_STACK_INT32; break;
            case ELEMENT_TYPE_I1:
                primitives[slot] = (Byte)value; refs[slot] = ExecutionStackPrimitiveMarker.EXECUTION_STACK_INT32; break;

            case ELEMENT_TYPE_CHAR:
            case ELEMENT_TYPE_U2:
                primitives[slot] = ((Short)value & 0xFFFF); refs[slot] = ExecutionStackPrimitiveMarker.EXECUTION_STACK_INT32; break;
            case ELEMENT_TYPE_I2:
                primitives[slot] = (Short)value; refs[slot] = ExecutionStackPrimitiveMarker.EXECUTION_STACK_INT32; break;

            case ELEMENT_TYPE_U4:
            case ELEMENT_TYPE_I4:
            case ELEMENT_TYPE_R4:
                primitives[slot] = (Integer)value; refs[slot] = ExecutionStackPrimitiveMarker.EXECUTION_STACK_INT32; break;

            case ELEMENT_TYPE_U8:
            case ELEMENT_TYPE_I8:
            case ELEMENT_TYPE_R8:
            case ELEMENT_TYPE_I:
            case ELEMENT_TYPE_U:
                primitives[slot] = (Long)value; refs[slot] = ExecutionStackPrimitiveMarker.EXECUTION_STACK_INT64; break;

            default:
                throw new BACILInternalError("Converting simple type to stack var not implemented: type " + typeCategory);
        }
    }
}
