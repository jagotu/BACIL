using System;

namespace ComponentParsingGeneral;

public interface AI {}
public class A {}

public class G<T> {}

public class Bar1
{
    void Bar1_Foo1() {}

    A Bar1_Foo2() { throw new NotImplementedException(); }

    void Bar1_Foo3(A p1) {}

    void Bar1_Foo4<T1>(G<T1> p1) {}

    void Bar1_Foo5<T1>(T1 p1)
        where T1 : A
    { }

    public virtual void Bar1_Foo6()
    {
        try
        {
            Bar1_Foo1();
        }
        catch
        {
            Bar1_Foo1();
        }
        finally
        {
            Bar1_Foo1();
        }
        return;
    }

    void Bar1_Foo7()
    {
        try
        {
            Bar1_Foo1();
        }
        catch (NotImplementedException ex)
        {
            Bar1_Foo1();
        }
//        finally
//        {
//            Bar1_Foo1();
//        }
    }

    void Bar1_Foo8()
    {
        try
        {
            Bar1_Foo1();
            try
            {
                Bar1_Foo1();
            }
            catch
            {
                Bar1_Foo1();
            }
//            finally
//            {
//                Bar1_Foo1();
//            }
        }
        catch
        {
            Bar1_Foo1();
        }
//        finally
//        {
//            Bar1_Foo1();
//        }

        Bar1_Foo1();
    }

    void Bar1_Foo9(G<A> p1, G<A> p2, A p3) {}

    void Bar1_Foo10<T>() where T : A, AI, new() {}
}

public class Bar2<T1>
{
    void Bar2_Foo1<T2>(T1 p1, T2 p2) {}
}