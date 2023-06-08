using System;

namespace SubstitutionTest;

class A1 {}

class G1a<T1> {}

class G1b<T1> : G1a<T1> {}

class G1c<T1>
{
    public T1 field1;
}

class G1d<T1>
{
    public void Foo(T1 a) {}
}

class G1e<T1> : G1d<G1d<T1>> {}

class Methods
{
    public void Bar() {}
    public void Foo<T1>(T1 p1) {}
    public T1 Foo1<T1>() {throw new NotImplementedException();}
    public void Foo2<T1>(T1 p1)
    {
        T1 a = p1;
        Foo(a);
        Foo(a);
        Foo(p1);
    }
    public void Foo3<T1>() where T1 : Exception
    {
        try
        {
            Bar();
        }
        catch (T1 ex)
        {
            Bar();
        }
    }

    public void Foo4<T1>() where T1 : G1a<T1> {}
}

interface II1<T1> {}
interface II2<T1> {}

public class G2a<T1, T2> : II1<T1>, II2<T2> {}