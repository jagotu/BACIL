namespace PrimitiveTypesTest;

public class IntClass
{
    int fieldInt;
}

public class UIntClass
{
    uint fieldUInt;
}

public class LongClass
{
    long fieldLong;
}

public class ULongClass
{
    ulong fieldULong;
}

public class ShortClass
{
    short fieldShort;
}

public class UShortClass
{
    ushort fieldUShort;
}

public class ByteClass
{
    byte fieldByte;
}

public class SByteClass
{
    sbyte fieldSByte;
}

public class FloatClass
{
    float fieldFloat;
}

public class DoubleClass
{
    double fieldDouble;
}

public class DecimalClass
{
    decimal fieldDecimal;
}

public class CharClass
{
    char fieldChar;
}

public class BoolClass
{
    bool fieldBool;
}

public class StringClass
{
    string fieldString;
}

public class ObjectClass
{
    object fieldObject;
}

public class DynamicClass
{
    dynamic fieldDynamic;
}

public class NullableIntClass
{
    int? fieldNullableInt;
}

public class VoidClass
{
    public void Method()
    {
    }
}

public class ArrayClass
{
    int[] fieldArray;
}

public class NestedArrayClass
{
    int[][] fieldNestedArray;
}


public class DoubleArrayClass
{
    int[,] fieldDoubleArray = new int[1, 1];
}

public class GenericArrayClass<T>
{
    T[] fieldGenericArray;
}