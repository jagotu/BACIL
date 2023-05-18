package com.vztekoverflow.cilostazol.runtime.typesystem;

import com.vztekoverflow.cil.parser.cli.signature.MethodDefFlags;
import com.vztekoverflow.cilostazol.CILOSTAZOLLanguage;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.typesystem.appdomain.AppDomain;
import com.vztekoverflow.cilostazol.runtime.typesystem.appdomain.IAppDomain;
import com.vztekoverflow.cilostazol.runtime.typesystem.assembly.Assembly;
import com.vztekoverflow.cilostazol.runtime.typesystem.assembly.IAssembly;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.GenericParameterFlags;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.IMethod;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.exceptionhandler.ExceptionClauseFlags;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.flags.MethodFlags;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.parameter.ParamFlags;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;
import org.graalvm.polyglot.Source;

import java.nio.file.Path;
import java.util.Arrays;

public class MethodMetadataTests  extends TestBase {
    private IMethod[] getMethod(IType type, String name)
    {
        return Arrays.stream(type.getMethods()).filter(m -> m.getName().equals(name)).toArray(IMethod[]::new);
    }

    private IAssembly getAssembly() throws Exception
    {
        final String projectName = "MethodMetadataTest";

        final CILOSTAZOLLanguage lang = new CILOSTAZOLLanguage();
        CILOSTAZOLContext ctx = new CILOSTAZOLContext(lang, new Path[0]);
        Source source = getSourceFromProject(projectName);

        IAppDomain domain = new AppDomain(ctx);
        IAssembly assembly = Assembly.parse(source);
        domain.loadAssembly(assembly);
        return assembly;
    }

    private IType getType(IAssembly assembly, String namespace, String klass)
    {
        return assembly.getLocalType(namespace, klass);
    }

    public void testMethod_Accessibility() throws Exception
    {
        IAssembly assembly = getAssembly();
        //class Accessibility
        IType type = getType(assembly, "MethodMetadataTest", "Accessibility");

        //public void Foo1(){}
        IMethod[] mFoo1 = getMethod(type, "Foo1");
        assertEquals(1, mFoo1.length);
        assertTrue(mFoo1[0].getMethodFlags().hasFlag(MethodFlags.Flag.PUBLIC));

        //private void Foo2() {}
        IMethod[] mFoo2 = getMethod(type, "Foo2");
        assertEquals(1, mFoo2.length);
        assertTrue(mFoo2[0].getMethodFlags().hasFlag(MethodFlags.Flag.PRIVATE));

        //internal void Foo3() {}
        IMethod[] mFoo3 = getMethod(type, "Foo3");
        assertEquals(1, mFoo3.length);
        assertTrue(mFoo3[0].getMethodFlags().hasFlag(MethodFlags.Flag.ASSEM));

        //protected void Foo4() {}
        IMethod[] mFoo4 = getMethod(type, "Foo4");
        assertTrue(mFoo4[0].getMethodFlags().hasFlag(MethodFlags.Flag.FAMILY));
    }

    public void testMethod_Virtual2() throws Exception
    {
        IAssembly assembly = getAssembly();
        //class Virtual2 : Virtual1
        IType type = getType(assembly, "MethodMetadataTest", "Virtual2");

        //public override void Foo1() {}
        IMethod[] mFoo1 = getMethod(type, "Foo1");
        assertEquals(1, mFoo1.length);
        assertTrue(mFoo1[0].getMethodFlags().hasFlag(MethodFlags.Flag.REUSE_SLOT));
        assertTrue(mFoo1[0].getMethodFlags().hasFlag(MethodFlags.Flag.VIRTUAL));

        //public virtual void Foo2() {}
        IMethod[] mFoo2 = getMethod(type, "Foo2");
        assertEquals(1, mFoo2.length);
        assertTrue(mFoo2[0].getMethodFlags().hasFlag(MethodFlags.Flag.NEW_SLOT));
        assertTrue(mFoo2[0].getMethodFlags().hasFlag(MethodFlags.Flag.VIRTUAL));

        //public new void Foo3() {}
        IMethod[] mFoo3 = getMethod(type, "Foo3");
        assertEquals(1, mFoo3.length);
        assertTrue(mFoo3[0].getMethodFlags().hasFlag(MethodFlags.Flag.HIDE_BY_SIG));

        //public override sealed void Foo4() {}
        IMethod[] mFoo4 = getMethod(type, "Foo4");
        assertEquals(1, mFoo4.length);
        assertTrue(mFoo4[0].getMethodFlags().hasFlag(MethodFlags.Flag.REUSE_SLOT));
        assertTrue(mFoo4[0].getMethodFlags().hasFlag(MethodFlags.Flag.VIRTUAL));
        assertTrue(mFoo4[0].getMethodFlags().hasFlag(MethodFlags.Flag.FINAL));

        //public virtual void Foo5() {}
        //TODO: make it available via MethodImpl
        //IMethod[] mFoo5 = getMethod(type, "Foo5");
        //assertEquals(1, mFoo5.length);
    }

    public void testMethod_Impl1() throws Exception
    {
        IAssembly assembly = getAssembly();
        //interface Impl1
        IType type = getType(assembly, "MethodMetadataTest", "Impl1");

        //public void Foo();
        IMethod[] mFoo = getMethod(type, "Foo");
        assertEquals(1, mFoo.length);
        assertTrue(mFoo[0].getMethodFlags().hasFlag(MethodFlags.Flag.ABSTRACT));
    }

    public void testMethod_Impl() throws Exception
    {
        IAssembly assembly = getAssembly();
        //class Impl : Impl1, Impl2
        IType type = getType(assembly, "MethodMetadataTest", "Impl");

        //void Impl1.Foo() {}
        IMethod[] mFoo1 = getMethod(type, "MethodMetadataTest.Impl1.Foo");
        assertEquals(1, mFoo1.length);
        //void Impl2.Foo() {}
        IMethod[] mFoo2 = getMethod(type, "MethodMetadataTest.Impl2.Foo");
        assertEquals(1, mFoo2.length);
    }

    public void testMethod_Static() throws Exception
    {
        IAssembly assembly = getAssembly();
        //class Static
        IType type = getType(assembly, "MethodMetadataTest", "Static");

        //public static void Foo(){}
        IMethod[] mFoo = getMethod(type, "Foo");
        assertEquals(1, mFoo.length);
        assertTrue(mFoo[0].getMethodFlags().hasFlag(MethodFlags.Flag.STATIC));
    }

    public void testMethod_ReturnType() throws Exception
    {
        IAssembly assembly = getAssembly();
        //class ReturnType
        IType type = getType(assembly, "MethodMetadataTest", "ReturnType");

        //public public void Foo() {}
        IMethod[] mFoo = getMethod(type, "Foo");
        assertEquals(1, mFoo.length);
        assertFalse(mFoo[0].getReturnType().isByRef());
        //TODO: assert return type

        //public ReturnT Foo1()
        IMethod[] mFoo1 = getMethod(type, "Foo1");
        assertEquals(1, mFoo1.length);
        assertFalse(mFoo1[0].getReturnType().isByRef());
        //TODO: assert return type

        //public ref ReturnT Foo2()
        IMethod[] mFoo2 = getMethod(type, "Foo2");
        assertEquals(1, mFoo2.length);
        assertTrue(mFoo2[0].getReturnType().isByRef());
        //TODO: assert return type
    }

    public void testMethod_Parameters() throws Exception
    {
        IAssembly assembly = getAssembly();
        //class Parameters
        IType type = getType(assembly, "MethodMetadataTest", "Parameters");

        //public void Foo() {}
        IMethod[] mFoo = getMethod(type, "Foo");
        assertEquals(2, mFoo.length);
        assertEquals(0, mFoo[0].getParameters().length);

        //public void Foo(Param1 p1) {}
        assertEquals(2, mFoo.length);
        assertEquals(1, mFoo[1].getParameters().length);
        assertTrue(mFoo[1].getMethodDefFlags().hasFlag(MethodDefFlags.Flag.HAS_THIS));
        assertFalse(mFoo[1].getMethodDefFlags().hasFlag(MethodDefFlags.Flag.EXPLICIT_THIS));
        assertTrue(mFoo[1].getMethodDefFlags().hasFlag(MethodDefFlags.Flag.DEFAULT));
        //Param1 p1
        assertEquals(1, mFoo[1].getParameters()[0].getIndex());
        assertFalse(mFoo[1].getParameters()[0].isByRef());
        assertEquals("p1", mFoo[1].getParameters()[0].getName());
        assertFalse(mFoo[1].getParameters()[0].getFlags().hasFlag(ParamFlags.Flag.HAS_DEFAULT));
        assertFalse(mFoo[1].getParameters()[0].getFlags().hasFlag(ParamFlags.Flag.OPTIONAL));
        //TODO: assert parameter type

        //public static void Foo1(Param1 p1) {}
        IMethod[] mFoo1 = getMethod(type, "Foo1");
        assertEquals(1, mFoo1.length);
        assertFalse(mFoo1[0].getMethodDefFlags().hasFlag(MethodDefFlags.Flag.HAS_THIS));

        //public static void Foo3(Param1 p1, params Param2[] ps) {}
        IMethod[] mFoo3 = getMethod(type, "Foo3");
        assertEquals(1, mFoo3.length);
        assertFalse(mFoo3[0].getMethodDefFlags().hasFlag(MethodDefFlags.Flag.VARARG));
        //Param1 p1, params Param2[] ps
        assertEquals(2, mFoo3[0].getParameters().length);
        assertEquals(1, mFoo3[0].getParameters()[0].getIndex());
        assertEquals(2, mFoo3[0].getParameters()[1].getIndex());
        assertEquals("p1", mFoo3[0].getParameters()[0].getName());
        assertEquals("ps", mFoo3[0].getParameters()[1].getName());
        //TODO: assert parameter types

        //public static void Foo4(ref Param1 p1, out Param1 p2, in Param1 p3)
        IMethod[] mFoo4 = getMethod(type, "Foo4");
        assertEquals(1, mFoo4.length);
        //ref Param1 p1, out Param1 p2, in Param1 p3)
        assertEquals(3, mFoo4[0].getParameters().length);
        assertEquals(1, mFoo4[0].getParameters()[0].getIndex());
        assertEquals(2, mFoo4[0].getParameters()[1].getIndex());
        assertEquals(3, mFoo4[0].getParameters()[2].getIndex());
        assertEquals("p1", mFoo4[0].getParameters()[0].getName());
        assertEquals("p2", mFoo4[0].getParameters()[1].getName());
        assertEquals("p3", mFoo4[0].getParameters()[2].getName());
        assertTrue(mFoo4[0].getParameters()[0].isByRef());
        assertTrue(mFoo4[0].getParameters()[1].getFlags().hasFlag(ParamFlags.Flag.OUT));
        assertTrue(mFoo4[0].getParameters()[2].getFlags().hasFlag(ParamFlags.Flag.IN));
        //TODO: assert parameter types


        //public void Foo5(Param1 p1 = null) {}
        IMethod[] mFoo5 = getMethod(type, "Foo5");
        assertEquals(1, mFoo5.length);
        assertEquals(1, mFoo5[0].getParameters()[0].getIndex());
        assertEquals("p1", mFoo5[0].getParameters()[0].getName());
        assertTrue(mFoo5[0].getParameters()[0].getFlags().hasFlag(ParamFlags.Flag.OPTIONAL));
        assertTrue(mFoo5[0].getParameters()[0].getFlags().hasFlag(ParamFlags.Flag.HAS_DEFAULT));
        //TODO: assert parameter type
    }

    public void testMethod_Extensions() throws Exception
    {
        IAssembly assembly = getAssembly();
        //class Extensions
        IType type = getType(assembly, "MethodMetadataTest", "Extensions");

        //public static void Foo2(this Parameters p1) {}
        IMethod[] mFoo2 = getMethod(type, "Foo2");
        assertEquals(1, mFoo2.length);
        assertFalse(mFoo2[0].getMethodDefFlags().hasFlag(MethodDefFlags.Flag.HAS_THIS));
        assertFalse(mFoo2[0].getMethodDefFlags().hasFlag(MethodDefFlags.Flag.EXPLICIT_THIS));
    }

    public void testMethod_TryBlocks() throws Exception
    {
        IAssembly assembly = getAssembly();
        //public class TryBlocks
        IType type = getType(assembly, "MethodMetadataTest", "TryBlocks");

        //public void Foo1()
        IMethod[] mFoo1 = getMethod(type, "Foo1");
        assertEquals(1, mFoo1.length);
        assertEquals(1, mFoo1[0].getExceptionHandlers().length);
        assertTrue(mFoo1[0].getExceptionHandlers()[0].getFlags().hasFlag(ExceptionClauseFlags.Flag.COR_ILEXCEPTION_CLAUSE_EXCEPTION));
        //TODO: assert type of exception

        //public void Foo2()
        IMethod[] mFoo2 = getMethod(type, "Foo2");
        assertEquals(1, mFoo2.length);
        assertEquals(1, mFoo2[0].getExceptionHandlers().length);
        assertTrue(mFoo2[0].getExceptionHandlers()[0].getFlags().hasFlag(ExceptionClauseFlags.Flag.COR_ILEXCEPTION_CLAUSE_EXCEPTION));
        //TODO: assert type of exception

        //public void Foo3()
        IMethod[] mFoo3 = getMethod(type, "Foo3");
        assertEquals(1, mFoo3.length);
        assertEquals(2, mFoo3[0].getExceptionHandlers().length);
        assertTrue(mFoo3[0].getExceptionHandlers()[0].getFlags().hasFlag(ExceptionClauseFlags.Flag.COR_ILEXCEPTION_CLAUSE_EXCEPTION));
        //TODO: assert type of exception
        assertTrue(mFoo3[0].getExceptionHandlers()[1].getFlags().hasFlag(ExceptionClauseFlags.Flag.COR_ILEXCEPTION_CLAUSE_FINALLY));

        //public void Foo4()
        IMethod[] mFoo4 = getMethod(type, "Foo4");
        assertEquals(1, mFoo4.length);
        assertEquals(2, mFoo4[0].getExceptionHandlers().length);
        assertTrue(mFoo4[0].getExceptionHandlers()[0].getFlags().hasFlag(ExceptionClauseFlags.Flag.COR_ILEXCEPTION_CLAUSE_EXCEPTION));
        //TODO: assert type of exception
        assertTrue(mFoo4[0].getExceptionHandlers()[1].getFlags().hasFlag(ExceptionClauseFlags.Flag.COR_ILEXCEPTION_CLAUSE_EXCEPTION));
        //TODO: assert type of exception

        //public void Foo5()
        IMethod[] mFoo5 = getMethod(type, "Foo5");
        assertEquals(1, mFoo5.length);
        assertEquals(3, mFoo5[0].getExceptionHandlers().length);
        assertTrue(mFoo5[0].getExceptionHandlers()[0].getFlags().hasFlag(ExceptionClauseFlags.Flag.COR_ILEXCEPTION_CLAUSE_EXCEPTION));
        //TODO: assert type of exception
        assertTrue(mFoo5[0].getExceptionHandlers()[1].getFlags().hasFlag(ExceptionClauseFlags.Flag.COR_ILEXCEPTION_CLAUSE_EXCEPTION));
        //TODO: assert type of exception
        assertTrue(mFoo5[0].getExceptionHandlers()[2].getFlags().hasFlag(ExceptionClauseFlags.Flag.COR_ILEXCEPTION_CLAUSE_EXCEPTION));
        //TODO: assert type of exception

        //public void Foo6()
        IMethod[] mFoo6= getMethod(type, "Foo6");
        assertEquals(1, mFoo6.length);
        assertEquals(1, mFoo6[0].getExceptionHandlers().length);
        assertTrue(mFoo6[0].getExceptionHandlers()[0].getFlags().hasFlag(ExceptionClauseFlags.Flag.COR_ILEXCEPTION_CLAUSE_FINALLY));
    }

    public void testMethod_Generics() throws Exception
    {
        IAssembly assembly = getAssembly();
        //class Generics
        IType type = getType(assembly, "MethodMetadataTest", "Generics`1");

        //public void Foo1<T1>() {}
        IMethod[] mFoo1 = getMethod(type, "Foo1");
        assertEquals(1, mFoo1.length);
        assertEquals(1, mFoo1[0].getTypeParameters().length);
        assertEquals("T1", mFoo1[0].getTypeParameters()[0].getName());
        assertEquals(0, mFoo1[0].getTypeParameters()[0].getIndex());
        assertEquals(0, mFoo1[0].getTypeParameters()[0].getTypeConstrains().length);
        assertTrue(mFoo1[0].getTypeParameters()[0].getFlags().hasFlag(GenericParameterFlags.Flag.NONE));

        //public T1 Foo2<T1>(T1 p1)
        IMethod[] mFoo2 = getMethod(type, "Foo2");
        assertEquals(2, mFoo2.length);
        assertEquals(1, mFoo2[0].getTypeParameters().length);
        assertEquals("T1", mFoo2[0].getTypeParameters()[0].getName());
        assertEquals(0, mFoo2[0].getTypeParameters()[0].getIndex());
        assertEquals(0, mFoo2[0].getTypeParameters()[0].getTypeConstrains().length);
        assertTrue(mFoo2[0].getTypeParameters()[0].getFlags().hasFlag(GenericParameterFlags.Flag.NONE));
        //TODO: assert type of the parameter
        //TODO: assert type of the return type

        //public T1 Foo2<T1, T2>(T1 p1, T2 p2)
        assertEquals(2, mFoo2[1].getTypeParameters().length);
        assertEquals("T1", mFoo2[1].getTypeParameters()[0].getName());
        assertEquals("T2", mFoo2[1].getTypeParameters()[1].getName());
        assertEquals(0, mFoo2[1].getTypeParameters()[0].getIndex());
        assertEquals(1, mFoo2[1].getTypeParameters()[1].getIndex());
        assertEquals(0, mFoo2[1].getTypeParameters()[0].getTypeConstrains().length);
        assertEquals(0, mFoo2[1].getTypeParameters()[1].getTypeConstrains().length);
        assertTrue(mFoo2[1].getTypeParameters()[0].getFlags().hasFlag(GenericParameterFlags.Flag.NONE));
        assertTrue(mFoo2[1].getTypeParameters()[1].getFlags().hasFlag(GenericParameterFlags.Flag.NONE));
        //TODO: assert type of parameters
        //TODO: assert type of the return type

        //public T1 Foo3<T1>(T1 p1, G1 p2)
        IMethod[] mFoo3 = getMethod(type, "Foo3");
        assertEquals(1, mFoo3.length);
        assertEquals(1, mFoo3[0].getTypeParameters().length);
        assertEquals("T1", mFoo3[0].getTypeParameters()[0].getName());
        assertEquals(0, mFoo3[0].getTypeParameters()[0].getIndex());
        assertEquals(0, mFoo3[0].getTypeParameters()[0].getTypeConstrains().length);
        assertTrue(mFoo3[0].getTypeParameters()[0].getFlags().hasFlag(GenericParameterFlags.Flag.NONE));
        ///TODO: assert type of parameters

        //public T1 Foo4<T1>(Param3<T1, G1> p1)
        IMethod[] mFoo4 = getMethod(type, "Foo4");
        assertEquals(1, mFoo4.length);
        ///TODO: assert type of parameters

        //public void Foo5<T1, T2>() where T2 : new() where T1 : Param3<T1, T2> {}
        IMethod[] mFoo5 = getMethod(type, "Foo5");
        assertEquals(1, mFoo5.length);
        assertEquals(2, mFoo5[0].getTypeParameters().length);
        assertEquals("T1", mFoo5[0].getTypeParameters()[0].getName());
        assertEquals("T2", mFoo5[0].getTypeParameters()[1].getName());
        assertEquals(0, mFoo5[0].getTypeParameters()[0].getIndex());
        assertEquals(1, mFoo5[0].getTypeParameters()[1].getIndex());
        assertEquals(1, mFoo5[0].getTypeParameters()[0].getTypeConstrains().length);
        //TODO: assert type of the constraint
        assertEquals(0, mFoo5[0].getTypeParameters()[1].getTypeConstrains().length);
        assertTrue(mFoo5[0].getTypeParameters()[0].getFlags().hasFlag(GenericParameterFlags.Flag.NONE));
        assertTrue(mFoo5[0].getTypeParameters()[1].getFlags().hasFlag(GenericParameterFlags.Flag.DEFAULT_CONSTRUCTOR_CONSTRAINT));

        //public void Foo6<T1>() where T1 : Exception
        IMethod[] mFoo6 = getMethod(type, "Foo6");
        assertEquals(1, mFoo6.length);
        assertEquals(1, mFoo6[0].getTypeParameters().length);
        assertEquals(1, mFoo6[0].getTypeParameters()[0].getTypeConstrains().length);
        //TODO: assert type of the constraint
        //TODO: assert type of the catch expression
    }

    public void testMethod_CTor() throws Exception
    {
        IAssembly assembly = getAssembly();
        //class CTor
        IType type = getType(assembly, "MethodMetadataTest", "CTor");

        //public CTor()
        IMethod[] ctor = getMethod(type, ".ctor");
        assertEquals(1, ctor.length);
        assertTrue(ctor[0].getMethodFlags().hasFlag(MethodFlags.Flag.SPECIAL_NAME));

        //static CTor()
        IMethod[] cctor = getMethod(type, ".cctor");
        assertEquals(1, cctor.length);
        assertTrue(cctor[0].getMethodFlags().hasFlag(MethodFlags.Flag.SPECIAL_NAME));

        //~CTor()
        IMethod[] Finalize = getMethod(type, "Finalize");
        assertEquals(1, cctor.length);
        assertTrue(cctor[0].getMethodFlags().hasFlag(MethodFlags.Flag.SPECIAL_NAME));
    }

    public void testMethod_Properties() throws Exception
    {
        IAssembly assembly = getAssembly();
        //class Properties
        IType type = getType(assembly, "MethodMetadataTest", "Properties");

        //public Prop1 prop1 {get;set;}
        IMethod[] get_prop1 = getMethod(type, "get_prop1");
        assertEquals(1, get_prop1.length);
        assertTrue(get_prop1[0].getMethodFlags().hasFlag(MethodFlags.Flag.SPECIAL_NAME));

        IMethod[] set_prop1 = getMethod(type, "set_prop1");
        assertEquals(1, set_prop1.length);
        assertTrue(set_prop1[0].getMethodFlags().hasFlag(MethodFlags.Flag.SPECIAL_NAME));

        //public Prop1 prop2 {get;}
        IMethod[] get_prop2 = getMethod(type, "get_prop2");
        assertEquals(1, get_prop2.length);
        assertTrue(get_prop2[0].getMethodFlags().hasFlag(MethodFlags.Flag.SPECIAL_NAME));

        IMethod[] set_prop2 = getMethod(type, "set_prop2");
        assertEquals(0, set_prop2.length);

        //public Prop1 prop3 {get;init;}
        IMethod[] get_prop3 = getMethod(type, "get_prop3");
        assertEquals(1, get_prop3.length);
        assertTrue(get_prop3[0].getMethodFlags().hasFlag(MethodFlags.Flag.SPECIAL_NAME));

        IMethod[] init_prop3 = getMethod(type, "set_prop3");
        assertEquals(1, init_prop3.length);
        assertTrue(init_prop3[0].getMethodFlags().hasFlag(MethodFlags.Flag.SPECIAL_NAME));
    }

    public void testMethod_Overload() throws Exception
    {
        IAssembly assembly = getAssembly();
        //class Overload
        IType type = getType(assembly, "MethodMetadataTest", "Overload");

        //public void Foo(Temp1 t) {},  public void Foo() {}
        IMethod[] mFoo = getMethod(type, "Foo");
        assertEquals(2, mFoo.length);
    }

    public void testMethod_Locals() throws Exception
    {
        IAssembly assembly = getAssembly();
        //class Locals
        IType type = getType(assembly, "MethodMetadataTest", "Locals");

        //public void Foo()
        IMethod[] mFoo = getMethod(type, "Foo");
        assertEquals(1, mFoo.length);
        assertEquals(2, mFoo[0].getLocals().length);
        assertFalse(mFoo[0].getLocals()[0].isByRef());
        assertFalse(mFoo[0].getLocals()[0].isPinned());
        //TODO: assert local type
        assertFalse(mFoo[0].getLocals()[1].isByRef());
        assertFalse(mFoo[0].getLocals()[1].isPinned());
        //TODO: assert local type
    }
}
