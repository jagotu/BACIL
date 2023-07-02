package com.vztekoverflow.cilostazol.runtime.typesystem;

import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cil.parser.cli.signature.MethodDefFlags;
import com.vztekoverflow.cilostazol.runtime.context.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.symbols.*;
import java.nio.file.Path;
import java.util.Arrays;

public class MethodMetadataTests extends TestBase {
  private MethodSymbol[] getMethod(NamedTypeSymbol type, String name) {
    return Arrays.stream(type.getMethods())
        .filter(m -> m.getName().equals(name))
        .toArray(MethodSymbol[]::new);
  }

  public void testMethod_Accessibility() {
    final String projectName = "MethodMetadataTest";
    final CILOSTAZOLContext ctx = init(new Path[] {getDllPath(projectName).getParent()});
    final AssemblyIdentity assemblyID = getAssemblyID(projectName);

    // class Accessibility
    NamedTypeSymbol type = ctx.getType("Accessibility", "MethodMetadataTest", assemblyID);

    // public void Foo1(){}
    MethodSymbol[] mFoo1 = getMethod(type, "Foo1");
    assertEquals(1, mFoo1.length);
    assertTrue(mFoo1[0].getMethodFlags().hasFlag(MethodSymbol.MethodFlags.Flag.PUBLIC));

    // private void Foo2() {}
    MethodSymbol[] mFoo2 = getMethod(type, "Foo2");
    assertEquals(1, mFoo2.length);
    assertTrue(mFoo2[0].getMethodFlags().hasFlag(MethodSymbol.MethodFlags.Flag.PRIVATE));

    // internal void Foo3() {}
    MethodSymbol[] mFoo3 = getMethod(type, "Foo3");
    assertEquals(1, mFoo3.length);
    assertTrue(mFoo3[0].getMethodFlags().hasFlag(MethodSymbol.MethodFlags.Flag.ASSEM));

    // protected void Foo4() {}
    MethodSymbol[] mFoo4 = getMethod(type, "Foo4");
    assertTrue(mFoo4[0].getMethodFlags().hasFlag(MethodSymbol.MethodFlags.Flag.FAMILY));
  }

  public void testMethod_Virtual2() {
    final String projectName = "MethodMetadataTest";
    final CILOSTAZOLContext ctx = init(new Path[] {getDllPath(projectName).getParent()});
    final AssemblyIdentity assemblyID = getAssemblyID(projectName);

    NamedTypeSymbol type = ctx.getType("Virtual2", "MethodMetadataTest", assemblyID);

    // public override void Foo1() {}
    MethodSymbol[] mFoo1 = getMethod(type, "Foo1");
    assertEquals(1, mFoo1.length);
    assertTrue(mFoo1[0].getMethodFlags().hasFlag(MethodSymbol.MethodFlags.Flag.REUSE_SLOT));
    assertTrue(mFoo1[0].getMethodFlags().hasFlag(MethodSymbol.MethodFlags.Flag.VIRTUAL));

    // public virtual void Foo2() {}
    MethodSymbol[] mFoo2 = getMethod(type, "Foo2");
    assertEquals(1, mFoo2.length);
    assertTrue(mFoo2[0].getMethodFlags().hasFlag(MethodSymbol.MethodFlags.Flag.NEW_SLOT));
    assertTrue(mFoo2[0].getMethodFlags().hasFlag(MethodSymbol.MethodFlags.Flag.VIRTUAL));

    // public new void Foo3() {}
    MethodSymbol[] mFoo3 = getMethod(type, "Foo3");
    assertEquals(1, mFoo3.length);
    assertTrue(mFoo3[0].getMethodFlags().hasFlag(MethodSymbol.MethodFlags.Flag.HIDE_BY_SIG));

    // public override sealed void Foo4() {}
    MethodSymbol[] mFoo4 = getMethod(type, "Foo4");
    assertEquals(1, mFoo4.length);
    assertTrue(mFoo4[0].getMethodFlags().hasFlag(MethodSymbol.MethodFlags.Flag.REUSE_SLOT));
    assertTrue(mFoo4[0].getMethodFlags().hasFlag(MethodSymbol.MethodFlags.Flag.VIRTUAL));
    assertTrue(mFoo4[0].getMethodFlags().hasFlag(MethodSymbol.MethodFlags.Flag.FINAL));

    // public virtual void Foo5() {}
    // TODO: make it available via MethodImpl
    // IMethod[] mFoo5 = getMethod(type, "Foo5");
    // assertEquals(1, mFoo5.length);
  }

  public void testMethod_Impl1() {
    final String projectName = "MethodMetadataTest";
    final CILOSTAZOLContext ctx = init(new Path[] {getDllPath(projectName).getParent()});
    final AssemblyIdentity assemblyID = getAssemblyID(projectName);

    // interface Impl1
    NamedTypeSymbol type = ctx.getType("Impl1", "MethodMetadataTest", assemblyID);

    // public void Foo();
    MethodSymbol[] mFoo = getMethod(type, "Foo");
    assertEquals(1, mFoo.length);
    assertTrue(mFoo[0].getMethodFlags().hasFlag(MethodSymbol.MethodFlags.Flag.ABSTRACT));
  }

  public void testMethod_Impl() {
    final String projectName = "MethodMetadataTest";
    final CILOSTAZOLContext ctx = init(new Path[] {getDllPath(projectName).getParent()});
    final AssemblyIdentity assemblyID = getAssemblyID(projectName);

    // class Impl : Impl1, Impl2
    NamedTypeSymbol type = ctx.getType("Impl", "MethodMetadataTest", assemblyID);

    // void Impl1.Foo() {}
    MethodSymbol[] mFoo1 = getMethod(type, "MethodMetadataTest.Impl1.Foo");
    assertEquals(1, mFoo1.length);
    // void Impl2.Foo() {}
    MethodSymbol[] mFoo2 = getMethod(type, "MethodMetadataTest.Impl2.Foo");
    assertEquals(1, mFoo2.length);
  }

  public void testMethod_Static() {
    final String projectName = "MethodMetadataTest";
    final CILOSTAZOLContext ctx = init(new Path[] {getDllPath(projectName).getParent()});
    final AssemblyIdentity assemblyID = getAssemblyID(projectName);

    // class Static
    NamedTypeSymbol type = ctx.getType("Static", "MethodMetadataTest", assemblyID);

    // public static void Foo(){}
    MethodSymbol[] mFoo = getMethod(type, "Foo");
    assertEquals(1, mFoo.length);
    assertTrue(mFoo[0].getMethodFlags().hasFlag(MethodSymbol.MethodFlags.Flag.STATIC));
  }

  public void testMethod_ReturnType() {
    final String projectName = "MethodMetadataTest";
    final CILOSTAZOLContext ctx = init(new Path[] {getDllPath(projectName).getParent()});
    final AssemblyIdentity assemblyID = getAssemblyID(projectName);

    // class ReturnType
    NamedTypeSymbol type = ctx.getType("ReturnType", "MethodMetadataTest", assemblyID);

    // public public void Foo() {}
    MethodSymbol[] mFoo = getMethod(type, "Foo");
    assertEquals(1, mFoo.length);
    assertFalse(mFoo[0].getReturnType().isByRef());
    assertNull(mFoo[0].getReturnType().getType());

    // public ReturnT Foo1()
    MethodSymbol[] mFoo1 = getMethod(type, "Foo1");
    assertEquals(1, mFoo1.length);
    assertFalse(mFoo1[0].getReturnType().isByRef());
    assertEquals(
        ctx.getType("ReturnT", "MethodMetadataTest", assemblyID),
        mFoo1[0].getReturnType().getType());

    // public ref ReturnT Foo2()
    MethodSymbol[] mFoo2 = getMethod(type, "Foo2");
    assertEquals(1, mFoo2.length);
    assertTrue(mFoo2[0].getReturnType().isByRef());
    assertEquals(
        ctx.getType("ReturnT", "MethodMetadataTest", assemblyID),
        mFoo1[0].getReturnType().getType());
  }

  public void testMethod_Parameters() {
    final String projectName = "MethodMetadataTest";
    final CILOSTAZOLContext ctx = init(new Path[] {getDllPath(projectName).getParent()});
    final AssemblyIdentity assemblyID = getAssemblyID(projectName);

    // class Parameters
    NamedTypeSymbol type = ctx.getType("Parameters", "MethodMetadataTest", assemblyID);

    // public void Foo() {}
    MethodSymbol[] mFoo = getMethod(type, "Foo");
    assertEquals(2, mFoo.length);
    assertEquals(0, mFoo[0].getParameters().length);

    // public void Foo(Param1 p1) {}
    assertEquals(2, mFoo.length);
    assertEquals(1, mFoo[1].getParameters().length);
    assertTrue(mFoo[1].getMethodDefFlags().hasFlag(MethodDefFlags.Flag.HAS_THIS));
    assertFalse(mFoo[1].getMethodDefFlags().hasFlag(MethodDefFlags.Flag.EXPLICIT_THIS));
    assertTrue(mFoo[1].getMethodDefFlags().hasFlag(MethodDefFlags.Flag.DEFAULT));
    // Param1 p1
    assertEquals(1, mFoo[1].getParameters()[0].getIndex());
    assertFalse(mFoo[1].getParameters()[0].isByRef());
    assertEquals("p1", mFoo[1].getParameters()[0].getName());
    assertFalse(
        mFoo[1].getParameters()[0].getFlags().hasFlag(ParameterSymbol.ParamFlags.Flag.HAS_DEFAULT));
    assertFalse(
        mFoo[1].getParameters()[0].getFlags().hasFlag(ParameterSymbol.ParamFlags.Flag.OPTIONAL));
    assertEquals(
        ctx.getType("Param1", "MethodMetadataTest", assemblyID),
        mFoo[1].getParameters()[0].getType());

    // public static void Foo1(Param1 p1) {}
    MethodSymbol[] mFoo1 = getMethod(type, "Foo1");
    assertEquals(1, mFoo1.length);
    assertFalse(mFoo1[0].getMethodDefFlags().hasFlag(MethodDefFlags.Flag.HAS_THIS));

    // public static void Foo3(Param1 p1, params Param2[] ps) {}
    MethodSymbol[] mFoo3 = getMethod(type, "Foo3");
    assertEquals(1, mFoo3.length);
    assertFalse(mFoo3[0].getMethodDefFlags().hasFlag(MethodDefFlags.Flag.VARARG));
    // Param1 p1, params Param2[] ps
    assertEquals(2, mFoo3[0].getParameters().length);
    assertEquals(1, mFoo3[0].getParameters()[0].getIndex());
    assertEquals(2, mFoo3[0].getParameters()[1].getIndex());
    assertEquals("p1", mFoo3[0].getParameters()[0].getName());
    assertEquals("ps", mFoo3[0].getParameters()[1].getName());
    // TODO: assert object type

    // public static void Foo4(ref Param1 p1, out Param1 p2, in Param1 p3)
    MethodSymbol[] mFoo4 = getMethod(type, "Foo4");
    assertEquals(1, mFoo4.length);
    // ref Param1 p1, out Param1 p2, in Param1 p3)
    assertEquals(3, mFoo4[0].getParameters().length);
    assertEquals(1, mFoo4[0].getParameters()[0].getIndex());
    assertEquals(2, mFoo4[0].getParameters()[1].getIndex());
    assertEquals(3, mFoo4[0].getParameters()[2].getIndex());
    assertEquals("p1", mFoo4[0].getParameters()[0].getName());
    assertEquals("p2", mFoo4[0].getParameters()[1].getName());
    assertEquals("p3", mFoo4[0].getParameters()[2].getName());
    assertTrue(mFoo4[0].getParameters()[0].isByRef());
    assertTrue(mFoo4[0].getParameters()[1].getFlags().hasFlag(ParameterSymbol.ParamFlags.Flag.OUT));
    assertTrue(mFoo4[0].getParameters()[2].getFlags().hasFlag(ParameterSymbol.ParamFlags.Flag.IN));
    assertEquals(
        ctx.getType("Param1", "MethodMetadataTest", assemblyID),
        mFoo4[0].getParameters()[0].getType());
    assertEquals(
        ctx.getType("Param1", "MethodMetadataTest", assemblyID),
        mFoo4[0].getParameters()[1].getType());
    assertEquals(
        ctx.getType("Param1", "MethodMetadataTest", assemblyID),
        mFoo4[0].getParameters()[2].getType());

    // public void Foo5(Param1 p1 = null) {}
    MethodSymbol[] mFoo5 = getMethod(type, "Foo5");
    assertEquals(1, mFoo5.length);
    assertEquals(1, mFoo5[0].getParameters()[0].getIndex());
    assertEquals("p1", mFoo5[0].getParameters()[0].getName());
    assertTrue(
        mFoo5[0].getParameters()[0].getFlags().hasFlag(ParameterSymbol.ParamFlags.Flag.OPTIONAL));
    assertTrue(
        mFoo5[0]
            .getParameters()[0]
            .getFlags()
            .hasFlag(ParameterSymbol.ParamFlags.Flag.HAS_DEFAULT));
    assertEquals(
        ctx.getType("Param1", "MethodMetadataTest", assemblyID),
        mFoo5[0].getParameters()[0].getType());
  }

  public void testMethod_Extensions() {
    final String projectName = "MethodMetadataTest";
    final CILOSTAZOLContext ctx = init(new Path[] {getDllPath(projectName).getParent()});
    final AssemblyIdentity assemblyID = getAssemblyID(projectName);

    // class Extensions
    NamedTypeSymbol type = ctx.getType("Extensions", "MethodMetadataTest", assemblyID);

    // public static void Foo2(this Parameters p1) {}
    MethodSymbol[] mFoo2 = getMethod(type, "Foo2");
    assertEquals(1, mFoo2.length);
    assertFalse(mFoo2[0].getMethodDefFlags().hasFlag(MethodDefFlags.Flag.HAS_THIS));
    assertFalse(mFoo2[0].getMethodDefFlags().hasFlag(MethodDefFlags.Flag.EXPLICIT_THIS));
  }

  public void testMethod_TryBlocks() {
    final String projectName = "MethodMetadataTest";
    final CILOSTAZOLContext ctx = init(new Path[] {getDllPath(projectName).getParent()});
    final AssemblyIdentity assemblyID = getAssemblyID(projectName);

    // public class TryBlocks
    NamedTypeSymbol type = ctx.getType("TryBlocks", "MethodMetadataTest", assemblyID);

    // public void Foo1()
    MethodSymbol[] mFoo1 = getMethod(type, "Foo1");
    assertEquals(1, mFoo1.length);
    assertEquals(1, mFoo1[0].getExceptionHandlers().length);
    assertTrue(
        mFoo1[0]
            .getExceptionHandlers()[0]
            .getFlags()
            .hasFlag(
                ExceptionHandlerSymbol.ExceptionClauseFlags.Flag.COR_ILEXCEPTION_CLAUSE_EXCEPTION));
    // TODO: assert type of exception

    // public void Foo2()
    MethodSymbol[] mFoo2 = getMethod(type, "Foo2");
    assertEquals(1, mFoo2.length);
    assertEquals(1, mFoo2[0].getExceptionHandlers().length);
    assertTrue(
        mFoo2[0]
            .getExceptionHandlers()[0]
            .getFlags()
            .hasFlag(
                ExceptionHandlerSymbol.ExceptionClauseFlags.Flag.COR_ILEXCEPTION_CLAUSE_EXCEPTION));
    // TODO: assert type of exception

    // public void Foo3()
    MethodSymbol[] mFoo3 = getMethod(type, "Foo3");
    assertEquals(1, mFoo3.length);
    assertEquals(2, mFoo3[0].getExceptionHandlers().length);
    assertTrue(
        mFoo3[0]
            .getExceptionHandlers()[0]
            .getFlags()
            .hasFlag(
                ExceptionHandlerSymbol.ExceptionClauseFlags.Flag.COR_ILEXCEPTION_CLAUSE_EXCEPTION));
    // TODO: assert type of exception
    assertTrue(
        mFoo3[0]
            .getExceptionHandlers()[1]
            .getFlags()
            .hasFlag(
                ExceptionHandlerSymbol.ExceptionClauseFlags.Flag.COR_ILEXCEPTION_CLAUSE_FINALLY));

    // public void Foo4()
    MethodSymbol[] mFoo4 = getMethod(type, "Foo4");
    assertEquals(1, mFoo4.length);
    assertEquals(2, mFoo4[0].getExceptionHandlers().length);
    assertTrue(
        mFoo4[0]
            .getExceptionHandlers()[0]
            .getFlags()
            .hasFlag(
                ExceptionHandlerSymbol.ExceptionClauseFlags.Flag.COR_ILEXCEPTION_CLAUSE_EXCEPTION));
    // TODO: assert type of exception
    assertTrue(
        mFoo4[0]
            .getExceptionHandlers()[1]
            .getFlags()
            .hasFlag(
                ExceptionHandlerSymbol.ExceptionClauseFlags.Flag.COR_ILEXCEPTION_CLAUSE_EXCEPTION));
    // TODO: assert type of exception

    // public void Foo5()
    MethodSymbol[] mFoo5 = getMethod(type, "Foo5");
    assertEquals(1, mFoo5.length);
    assertEquals(3, mFoo5[0].getExceptionHandlers().length);
    assertTrue(
        mFoo5[0]
            .getExceptionHandlers()[0]
            .getFlags()
            .hasFlag(
                ExceptionHandlerSymbol.ExceptionClauseFlags.Flag.COR_ILEXCEPTION_CLAUSE_EXCEPTION));
    // TODO: assert type of exception
    assertTrue(
        mFoo5[0]
            .getExceptionHandlers()[1]
            .getFlags()
            .hasFlag(
                ExceptionHandlerSymbol.ExceptionClauseFlags.Flag.COR_ILEXCEPTION_CLAUSE_EXCEPTION));
    // TODO: assert type of exception
    assertTrue(
        mFoo5[0]
            .getExceptionHandlers()[2]
            .getFlags()
            .hasFlag(
                ExceptionHandlerSymbol.ExceptionClauseFlags.Flag.COR_ILEXCEPTION_CLAUSE_EXCEPTION));
    // TODO: assert type of exception

    // public void Foo6()
    MethodSymbol[] mFoo6 = getMethod(type, "Foo6");
    assertEquals(1, mFoo6.length);
    assertEquals(1, mFoo6[0].getExceptionHandlers().length);
    assertTrue(
        mFoo6[0]
            .getExceptionHandlers()[0]
            .getFlags()
            .hasFlag(
                ExceptionHandlerSymbol.ExceptionClauseFlags.Flag.COR_ILEXCEPTION_CLAUSE_FINALLY));
  }

  public void testMethod_Generics() {
    final String projectName = "MethodMetadataTest";
    final CILOSTAZOLContext ctx = init(new Path[] {getDllPath(projectName).getParent()});
    final AssemblyIdentity assemblyID = getAssemblyID(projectName);

    // class Generics
    NamedTypeSymbol type = ctx.getType("Generics`1", "MethodMetadataTest", assemblyID);

    // public void Foo1<T1>() {}
    MethodSymbol[] mFoo1 = getMethod(type, "Foo1");
    assertEquals(1, mFoo1.length);
    assertEquals(1, mFoo1[0].getTypeParameters().length);
    assertEquals("T1", mFoo1[0].getTypeParameters()[0].getName());
    assertEquals(0, mFoo1[0].getTypeParameters()[0].getOrdinal());
    assertEquals(0, mFoo1[0].getTypeParameters()[0].getTypeConstrains().length);
    assertTrue(
        mFoo1[0]
            .getTypeParameters()[0]
            .getFlags()
            .hasFlag(TypeParameterSymbol.GenericParameterFlags.Flag.NONE));

    // public T1 Foo2<T1>(T1 p1)
    MethodSymbol[] mFoo2 = getMethod(type, "Foo2");
    assertEquals(2, mFoo2.length);
    assertEquals(1, mFoo2[0].getTypeParameters().length);
    assertEquals("T1", mFoo2[0].getTypeParameters()[0].getName());
    assertEquals(0, mFoo2[0].getTypeParameters()[0].getOrdinal());
    assertEquals(0, mFoo2[0].getTypeParameters()[0].getTypeConstrains().length);
    assertTrue(
        mFoo2[0]
            .getTypeParameters()[0]
            .getFlags()
            .hasFlag(TypeParameterSymbol.GenericParameterFlags.Flag.NONE));
    // TODO: assert type of the parameter
    // TODO: assert type of the return type

    // public T1 Foo2<T1, T2>(T1 p1, T2 p2)
    assertEquals(2, mFoo2[1].getTypeParameters().length);
    assertEquals("T1", mFoo2[1].getTypeParameters()[0].getName());
    assertEquals("T2", mFoo2[1].getTypeParameters()[1].getName());
    assertEquals(0, mFoo2[1].getTypeParameters()[0].getOrdinal());
    assertEquals(1, mFoo2[1].getTypeParameters()[1].getOrdinal());
    assertEquals(0, mFoo2[1].getTypeParameters()[0].getTypeConstrains().length);
    assertEquals(0, mFoo2[1].getTypeParameters()[1].getTypeConstrains().length);
    assertTrue(
        mFoo2[1]
            .getTypeParameters()[0]
            .getFlags()
            .hasFlag(TypeParameterSymbol.GenericParameterFlags.Flag.NONE));
    assertTrue(
        mFoo2[1]
            .getTypeParameters()[1]
            .getFlags()
            .hasFlag(TypeParameterSymbol.GenericParameterFlags.Flag.NONE));
    // TODO: assert type of parameters
    // TODO: assert type of the return type

    // public T1 Foo3<T1>(T1 p1, G1 p2)
    MethodSymbol[] mFoo3 = getMethod(type, "Foo3");
    assertEquals(1, mFoo3.length);
    assertEquals(1, mFoo3[0].getTypeParameters().length);
    assertEquals("T1", mFoo3[0].getTypeParameters()[0].getName());
    assertEquals(0, mFoo3[0].getTypeParameters()[0].getOrdinal());
    assertEquals(0, mFoo3[0].getTypeParameters()[0].getTypeConstrains().length);
    assertTrue(
        mFoo3[0]
            .getTypeParameters()[0]
            .getFlags()
            .hasFlag(TypeParameterSymbol.GenericParameterFlags.Flag.NONE));
    /// TODO: assert type of parameters

    // public T1 Foo4<T1>(Param3<T1, G1> p1)
    MethodSymbol[] mFoo4 = getMethod(type, "Foo4");
    assertEquals(1, mFoo4.length);
    /// TODO: assert type of parameters

    // public void Foo5<T1, T2>() where T2 : new() where T1 : Param3<T1, T2> {}
    MethodSymbol[] mFoo5 = getMethod(type, "Foo5");
    assertEquals(1, mFoo5.length);
    assertEquals(2, mFoo5[0].getTypeParameters().length);
    assertEquals("T1", mFoo5[0].getTypeParameters()[0].getName());
    assertEquals("T2", mFoo5[0].getTypeParameters()[1].getName());
    assertEquals(0, mFoo5[0].getTypeParameters()[0].getOrdinal());
    assertEquals(1, mFoo5[0].getTypeParameters()[1].getOrdinal());
    assertEquals(1, mFoo5[0].getTypeParameters()[0].getTypeConstrains().length);
    // TODO: assert type of the constraint
    assertEquals(0, mFoo5[0].getTypeParameters()[1].getTypeConstrains().length);
    assertTrue(
        mFoo5[0]
            .getTypeParameters()[0]
            .getFlags()
            .hasFlag(TypeParameterSymbol.GenericParameterFlags.Flag.NONE));
    assertTrue(
        mFoo5[0]
            .getTypeParameters()[1]
            .getFlags()
            .hasFlag(
                TypeParameterSymbol.GenericParameterFlags.Flag.DEFAULT_CONSTRUCTOR_CONSTRAINT));

    // public void Foo6<T1>() where T1 : Exception
    MethodSymbol[] mFoo6 = getMethod(type, "Foo6");
    assertEquals(1, mFoo6.length);
    assertEquals(1, mFoo6[0].getTypeParameters().length);
    assertEquals(1, mFoo6[0].getTypeParameters()[0].getTypeConstrains().length);
    // TODO: assert type of the constraint
    // TODO: assert type of the catch expression
  }

  public void testMethod_CTor() {
    final String projectName = "MethodMetadataTest";
    final CILOSTAZOLContext ctx = init(new Path[] {getDllPath(projectName).getParent()});
    final AssemblyIdentity assemblyID = getAssemblyID(projectName);

    // class CTor
    NamedTypeSymbol type = ctx.getType("CTor", "MethodMetadataTest", assemblyID);

    // public CTor()
    MethodSymbol[] ctor = getMethod(type, ".ctor");
    assertEquals(1, ctor.length);
    assertTrue(ctor[0].getMethodFlags().hasFlag(MethodSymbol.MethodFlags.Flag.SPECIAL_NAME));

    // static CTor()
    MethodSymbol[] cctor = getMethod(type, ".cctor");
    assertEquals(1, cctor.length);
    assertTrue(cctor[0].getMethodFlags().hasFlag(MethodSymbol.MethodFlags.Flag.SPECIAL_NAME));

    // ~CTor()
    MethodSymbol[] Finalize = getMethod(type, "Finalize");
    assertEquals(1, cctor.length);
    assertTrue(cctor[0].getMethodFlags().hasFlag(MethodSymbol.MethodFlags.Flag.SPECIAL_NAME));
  }

  public void testMethod_Properties() {
    final String projectName = "MethodMetadataTest";
    final CILOSTAZOLContext ctx = init(new Path[] {getDllPath(projectName).getParent()});
    final AssemblyIdentity assemblyID = getAssemblyID(projectName);

    // class Properties
    NamedTypeSymbol type = ctx.getType("Properties", "MethodMetadataTest", assemblyID);

    // public Prop1 prop1 {get;set;}
    MethodSymbol[] get_prop1 = getMethod(type, "get_prop1");
    assertEquals(1, get_prop1.length);
    assertTrue(get_prop1[0].getMethodFlags().hasFlag(MethodSymbol.MethodFlags.Flag.SPECIAL_NAME));

    MethodSymbol[] set_prop1 = getMethod(type, "set_prop1");
    assertEquals(1, set_prop1.length);
    assertTrue(set_prop1[0].getMethodFlags().hasFlag(MethodSymbol.MethodFlags.Flag.SPECIAL_NAME));

    // public Prop1 prop2 {get;}
    MethodSymbol[] get_prop2 = getMethod(type, "get_prop2");
    assertEquals(1, get_prop2.length);
    assertTrue(get_prop2[0].getMethodFlags().hasFlag(MethodSymbol.MethodFlags.Flag.SPECIAL_NAME));

    MethodSymbol[] set_prop2 = getMethod(type, "set_prop2");
    assertEquals(0, set_prop2.length);

    // public Prop1 prop3 {get;init;}
    MethodSymbol[] get_prop3 = getMethod(type, "get_prop3");
    assertEquals(1, get_prop3.length);
    assertTrue(get_prop3[0].getMethodFlags().hasFlag(MethodSymbol.MethodFlags.Flag.SPECIAL_NAME));

    MethodSymbol[] init_prop3 = getMethod(type, "set_prop3");
    assertEquals(1, init_prop3.length);
    assertTrue(init_prop3[0].getMethodFlags().hasFlag(MethodSymbol.MethodFlags.Flag.SPECIAL_NAME));
  }

  public void testMethod_Overload() {
    final String projectName = "MethodMetadataTest";
    final CILOSTAZOLContext ctx = init(new Path[] {getDllPath(projectName).getParent()});
    final AssemblyIdentity assemblyID = getAssemblyID(projectName);

    // class Overload
    NamedTypeSymbol type = ctx.getType("Overload", "MethodMetadataTest", assemblyID);

    // public void Foo(Temp1 t) {},  public void Foo() {}
    MethodSymbol[] mFoo = getMethod(type, "Foo");
    assertEquals(2, mFoo.length);
  }

  public void testMethod_Locals() {
    final String projectName = "MethodMetadataTest";
    final CILOSTAZOLContext ctx = init(new Path[] {getDllPath(projectName).getParent()});
    final AssemblyIdentity assemblyID = getAssemblyID(projectName);

    // class Locals
    NamedTypeSymbol type = ctx.getType("Locals", "MethodMetadataTest", assemblyID);

    // public void Foo()
    MethodSymbol[] mFoo = getMethod(type, "Foo");
    assertEquals(1, mFoo.length);
    assertEquals(2, mFoo[0].getLocals().length);
    assertFalse(mFoo[0].getLocals()[0].isByRef());
    assertFalse(mFoo[0].getLocals()[0].isPinned());
    // TODO: assert local type
    assertFalse(mFoo[0].getLocals()[1].isByRef());
    assertFalse(mFoo[0].getLocals()[1].isPinned());
    // TODO: assert local type
  }
}
