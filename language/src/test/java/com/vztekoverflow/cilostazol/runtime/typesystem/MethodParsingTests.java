package com.vztekoverflow.cilostazol.runtime.typesystem;

import com.vztekoverflow.cil.parser.cli.CLIFileUtils;
import com.vztekoverflow.cil.parser.cli.table.generated.CLIMethodDefTableRow;
import com.vztekoverflow.cilostazol.CILOSTAZOLLanguage;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.typesystem.appdomain.AppDomain;
import com.vztekoverflow.cilostazol.runtime.typesystem.appdomain.IAppDomain;
import com.vztekoverflow.cilostazol.runtime.typesystem.assembly.Assembly;
import com.vztekoverflow.cilostazol.runtime.typesystem.assembly.IAssembly;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.IMethod;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.factory.MethodFactory;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;
import org.graalvm.polyglot.Source;
import org.junit.Assert;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

public class MethodParsingTests extends TestBase {
    public void testMethodParsingGeneral() throws Exception {
        final String projectName = "MethodParsingGeneral";

        final CILOSTAZOLLanguage lang = new CILOSTAZOLLanguage();
        CILOSTAZOLContext ctx = new CILOSTAZOLContext(lang, new Path[0]);
        Source source = getSourceFromProject(projectName);


        final IAppDomain domain = new AppDomain(ctx);
        final IAssembly assembly = Assembly.parse(source);
        domain.loadAssembly(assembly);

        final IComponent component = assembly.getComponents()[0];
        //Classes
        final IType classA = component.getLocalType("MethodParsingGeneral", "A");
        //final IType interfaceAI= component.getLocalType("MethodParsingGeneral","AI");
        final IType classG_T = component.getLocalType("MethodParsingGeneral", "G`1");
        final IType classBar1 = component.getLocalType("MethodParsingGeneral", "Bar1");
        final IType classBar2 = component.getLocalType("MethodParsingGeneral", "Bar2`1");

        final CLIMethodDefTableRow method_def_Bar1_Foo1 = CLIFileUtils.getMethodByName("Bar1_Foo1", component.getDefiningFile())[0];
        final CLIMethodDefTableRow method_def_Bar1_Foo2 = CLIFileUtils.getMethodByName("Bar1_Foo2", component.getDefiningFile())[0];
        final CLIMethodDefTableRow method_def_Bar1_Foo3 = CLIFileUtils.getMethodByName("Bar1_Foo3", component.getDefiningFile())[0];
        final CLIMethodDefTableRow method_def_Bar1_Foo4 = CLIFileUtils.getMethodByName("Bar1_Foo4", component.getDefiningFile())[0];
        final CLIMethodDefTableRow method_def_Bar1_Foo5 = CLIFileUtils.getMethodByName("Bar1_Foo5", component.getDefiningFile())[0];
        final CLIMethodDefTableRow method_def_Bar1_Foo6 = CLIFileUtils.getMethodByName("Bar1_Foo6", component.getDefiningFile())[0];
        final CLIMethodDefTableRow method_def_Bar1_Foo7 = CLIFileUtils.getMethodByName("Bar1_Foo7", component.getDefiningFile())[0];
        final CLIMethodDefTableRow method_def_Bar1_Foo8 = CLIFileUtils.getMethodByName("Bar1_Foo8", component.getDefiningFile())[0];
        final CLIMethodDefTableRow method_def_Bar1_Foo9 = CLIFileUtils.getMethodByName("Bar1_Foo9", component.getDefiningFile())[0];
        final CLIMethodDefTableRow method_def_Bar1_Foo10 = CLIFileUtils.getMethodByName("Bar1_Foo10", component.getDefiningFile())[0];
        final CLIMethodDefTableRow method_def_Bar2_Foo1 = CLIFileUtils.getMethodByName("Bar2_Foo1", component.getDefiningFile())[0];

        final IMethod method_Bar1_Foo1 = MethodFactory.create(method_def_Bar1_Foo1, classBar1);
        Assert.assertEquals(method_Bar1_Foo1.getName(), "Bar1_Foo1");
        final IMethod method_Bar1_Foo2 = MethodFactory.create(method_def_Bar1_Foo2, classBar1);
        final IMethod method_Bar1_Foo3 = MethodFactory.create(method_def_Bar1_Foo3, classBar1);
        final IMethod method_Bar1_Foo4 = MethodFactory.create(method_def_Bar1_Foo4, classBar1);
        final IMethod method_Bar1_Foo5 = MethodFactory.create(method_def_Bar1_Foo5, classBar1);
        final IMethod method_Bar1_Foo6 = MethodFactory.create(method_def_Bar1_Foo6, classBar1);
        final IMethod method_Bar1_Foo7 = MethodFactory.create(method_def_Bar1_Foo7, classBar1);
        final IMethod method_Bar1_Foo8 = MethodFactory.create(method_def_Bar1_Foo8, classBar1);
        final IMethod method_Bar1_Foo9 = MethodFactory.create(method_def_Bar1_Foo9, classBar1);
        final IMethod method_Bar1_Foo10 = MethodFactory.create(method_def_Bar1_Foo10, classBar1);
        final IMethod method_Bar2_Foo1 = MethodFactory.create(method_def_Bar2_Foo1, classBar2);
    }

    public void testMethodParsing_MethodExistence() throws IOException {
        final String projectName = "MethodParsingGeneral";

        final CILOSTAZOLLanguage lang = new CILOSTAZOLLanguage();
        CILOSTAZOLContext ctx = new CILOSTAZOLContext(lang, new Path[0]);
        Source source = getSourceFromProject(projectName);
        final IAppDomain domain = new AppDomain(ctx);
        final IAssembly assembly = Assembly.parse(source);
        domain.loadAssembly(assembly);
        IType type = assembly.getLocalType(projectName, "A");


        assertTrue(Arrays.stream(type.getMethods()).anyMatch(m -> m.getName().equals("fooPrivate")));
        assertTrue(Arrays.stream(type.getMethods()).anyMatch(m -> m.getName().equals("fooPublic")));
        assertTrue(Arrays.stream(type.getMethods()).anyMatch(m -> m.getName().equals("fooProtected")));
        assertTrue(Arrays.stream(type.getMethods()).anyMatch(m -> m.getName().equals("fooDefault")));
        //TODO: test args?
        assertTrue(Arrays.stream(type.getMethods()).anyMatch(m -> m.getName().equals("fooWithArg")));
        //TODO: test args?
        assertTrue(Arrays.stream(type.getMethods()).anyMatch(m -> m.getName().equals("fooWithArgs")));
        //TODO: test return type?
        assertTrue(Arrays.stream(type.getMethods()).anyMatch(m -> m.getName().equals("fooWithReturnType")));
        //TODO: test return type?
        assertTrue(Arrays.stream(type.getMethods()).anyMatch(m -> m.getName().equals("fooWithReturnTypeSelf")));
        //TODO: test return body?
        assertTrue(Arrays.stream(type.getMethods()).anyMatch(m -> m.getName().equals("fooWithExpressionBody")));
    }

}
