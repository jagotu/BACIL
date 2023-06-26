package com.vztekoverflow.cilostazol.runtime.typesystem;

import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cil.parser.cli.CLIFileUtils;
import com.vztekoverflow.cil.parser.cli.table.generated.CLIMethodDefTableRow;
import com.vztekoverflow.cilostazol.CILOSTAZOLLanguage;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.symbols.NamedTypeSymbol;
import com.vztekoverflow.cilostazol.runtime.typesystem.appdomain.AppDomain;
import com.vztekoverflow.cilostazol.runtime.typesystem.appdomain.IAppDomain;
import com.vztekoverflow.cilostazol.runtime.typesystem.assembly.Assembly;
import com.vztekoverflow.cilostazol.runtime.typesystem.assembly.IAssembly;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.CLIComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.IMethod;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.factory.MethodFactory;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.CLIType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import org.graalvm.polyglot.Source;
import org.junit.Assert;

public class MethodParsingTests extends TestBase {
  public void testMethodParsingGeneral() throws Exception {
    final String projectName = "MethodParsingGeneral";
    final CILOSTAZOLContext ctx = init(new Path[] {getDllPath(projectName).getParent()});
    final AssemblyIdentity assemblyID = getAssemblyID(projectName);

    // Classes
    final NamedTypeSymbol classA = ctx.getType("A", "MethodParsingGeneral", assemblyID);
    final NamedTypeSymbol interfaceAI = ctx.getType("AI", "MethodParsingGeneral", assemblyID);
    final NamedTypeSymbol classG_T = ctx.getType("G`1", "MethodParsingGeneral", assemblyID);
    final NamedTypeSymbol classBar1 = ctx.getType("Bar1", "MethodParsingGeneral", assemblyID);
    final NamedTypeSymbol classBar2 = ctx.getType("Bar2`1", "MethodParsingGeneral", assemblyID);

    // Methods
    Assert.assertTrue(
        Arrays.stream(classBar1.getMethods()).anyMatch(x -> x.getName().equals("Bar1_Foo1")));
    Assert.assertTrue(
        Arrays.stream(classBar1.getMethods()).anyMatch(x -> x.getName().equals("Bar1_Foo2")));
    Assert.assertTrue(
        Arrays.stream(classBar1.getMethods()).anyMatch(x -> x.getName().equals("Bar1_Foo3")));
    Assert.assertTrue(
        Arrays.stream(classBar1.getMethods()).anyMatch(x -> x.getName().equals("Bar1_Foo4")));
    Assert.assertTrue(
        Arrays.stream(classBar1.getMethods()).anyMatch(x -> x.getName().equals("Bar1_Foo5")));
    Assert.assertTrue(
        Arrays.stream(classBar1.getMethods()).anyMatch(x -> x.getName().equals("Bar1_Foo6")));
    Assert.assertTrue(
        Arrays.stream(classBar1.getMethods()).anyMatch(x -> x.getName().equals("Bar1_Foo7")));
    Assert.assertTrue(
        Arrays.stream(classBar1.getMethods()).anyMatch(x -> x.getName().equals("Bar1_Foo8")));
    Assert.assertTrue(
        Arrays.stream(classBar1.getMethods()).anyMatch(x -> x.getName().equals("Bar1_Foo9")));
    Assert.assertTrue(
        Arrays.stream(classBar1.getMethods()).anyMatch(x -> x.getName().equals("Bar1_Foo10")));
    Assert.assertTrue(
        Arrays.stream(classBar2.getMethods()).anyMatch(x -> x.getName().equals("Bar2_Foo1")));
  }

  public void testMethodParsing_MethodExistence() throws IOException {
    final String projectName = "MethodParsingGeneral";
    final CILOSTAZOLContext ctx = init(new Path[] {getDllPath(projectName).getParent()});
    final AssemblyIdentity assemblyID = getAssemblyID(projectName);

    final NamedTypeSymbol type = ctx.getType("A", "MethodParsingGeneral", assemblyID);

    assertTrue(Arrays.stream(type.getMethods()).anyMatch(m -> m.getName().equals("fooPrivate")));
    assertTrue(Arrays.stream(type.getMethods()).anyMatch(m -> m.getName().equals("fooPublic")));
    assertTrue(Arrays.stream(type.getMethods()).anyMatch(m -> m.getName().equals("fooProtected")));
    assertTrue(Arrays.stream(type.getMethods()).anyMatch(m -> m.getName().equals("fooDefault")));
    assertTrue(Arrays.stream(type.getMethods()).anyMatch(m -> m.getName().equals("fooWithArg")));
    assertTrue(Arrays.stream(type.getMethods()).anyMatch(m -> m.getName().equals("fooWithArgs")));
    assertTrue(
        Arrays.stream(type.getMethods()).anyMatch(m -> m.getName().equals("fooWithReturnType")));
    assertTrue(
        Arrays.stream(type.getMethods())
            .anyMatch(m -> m.getName().equals("fooWithReturnTypeSelf")));
    assertTrue(
        Arrays.stream(type.getMethods())
            .anyMatch(m -> m.getName().equals("fooWithExpressionBody")));
  }
}
