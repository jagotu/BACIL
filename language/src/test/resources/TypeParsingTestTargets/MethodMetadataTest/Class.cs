using System;

namespace MethodMetadataTest;

class Accessibility
{
    public void Foo1(){}
    private void Foo2() {}
    internal void Foo3() {}
    protected void Foo4() {}
}

class Virtual1
{
    public virtual void Foo1() {}
    public void Foo3(){}
    public virtual void Foo4() {}
}

class Virtual2 : Virtual1
{
    public override void Foo1() {}
    public virtual void Foo2() {}
    public new void Foo3() {}
    public override sealed void Foo4() {}
}

interface Impl1
{
    public void Foo();
}

interface Impl2
{
    public void Foo();
}

class Impl : Impl1, Impl2
{
    void Impl1.Foo() {}
    void Impl2.Foo() {}
}

class Static
{
    public static void Foo(){}
}

abstract class Abstract
{
    public abstract void Foo();
}

class ReturnT {}

class ReturnType
{
    public void Foo() {}
    public ReturnT Foo1()
    {
        throw new NotImplementedException();
    }
    public ref ReturnT Foo2()
    {
        throw new NotImplementedException();
    }
}

public class Param1 {}
public class Param2 {}

public class Parameters
{
    public void Foo() {}
    public void Foo(Param1 p1) {}
    public static void Foo1(Param1 p1) {}
    public static void Foo3(Param1 p1, params Param2[] ps) {}
    public static void Foo4(ref Param1 p1, out Param1 p2, in Param1 p3)
    {
        throw new NotImplementedException();
    }
}

public static class Extensions
{
    public static void Foo2(this Parameters p1) {}
}

public class TryBlocks
{
    public void Bar() {}
    public void Foo1()
    {
        try
        {
            Bar();
        }
        catch
        {
            Bar();
        }
    }

    public void Foo2()
    {
        try
        {
            Bar();
        }
        catch (Exception ex)
        {
            Bar();
        }
    }

    public void Foo3()
    {
        try
        {
            Bar();
        }
        catch
        {
            Bar();
        }
        finally
        {
            Bar();
        }
    }

    public void Foo4()
    {
        try
        {
            Bar();
        }
        catch (NotImplementedException ex)
        {
            Bar();
        }
        catch (Exception ex)
        {
            Bar();
        }
    }

    public void Foo5()
    {
        try
        {
            try
            {
                Bar();
            }
            catch
            {
                Bar();
            }
        }
        catch
        {
            try
            {
                Bar();
            }
            catch
            {
                Bar();
            }
        }
    }
}

class Param3<T1, T2> {}

class Generics<G1>
{
    public void Bar() {}
    public void Foo1<T1>() {}
    public T1 Foo2<T1>(T1 p1)
    {
        throw new NotImplementedException();
    }

    public T1 Foo2<T1, T2>(T1 p1, T2 p2)
    {
        throw new NotImplementedException();
    }

    public T1 Foo3<T1>(T1 p1, G1 p2)
    {
        throw new NotImplementedException();
    }

    public T1 Foo4<T1>(Param3<T1, G1> p1)
    {
        throw new NotImplementedException();
    }

    public void Foo5<T1, T2>() where T1 : Param3<T1, T2> {}

    public void Foo6<T1>() where T1 : Exception
    {
        try
        {
            Bar();
        }
        catch (T1 ex)
        {}
    }
}

class CTor
{
    public static void Bar() {}

    static CTor()
    {
        Bar();
    }

    public CTor()
    {
        Bar();
    }

    ~CTor()
    {
        Bar();
    }
}

class Prop1 {}

class Properties
{
    public Prop1 prop1 {get;set;}
    public Prop1 prop2 {get;}
    public Prop1 prop3 {get;init;}
}