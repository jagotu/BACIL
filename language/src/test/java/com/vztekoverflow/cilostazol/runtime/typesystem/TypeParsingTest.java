package com.vztekoverflow.cilostazol.runtime.typesystem;

import com.oracle.truffle.api.nodes.RootNode;
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
import junit.framework.TestCase;
import org.graalvm.polyglot.Source;
import org.junit.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * You have to first build C# projects in test resources:  {@value  _directory}.
 * <p>
 * Build it with configuration: {@value _configuration} and .NET version: {@value _dotnetVersion}
 */
public class TypeParsingTest extends TestCase {
    private static final String _directory = "src/test/resources/TypeParsingTestTargets";
    private static final String _configuration = "Release";
    private static final String _dotnetVersion = "net7.0";

    private Path getDllPath(String projectName) {
        return Paths.get(_directory, projectName, String.format("bin/%s/%s", _configuration, _dotnetVersion), projectName + ".dll");
    }

    public void testMethodParsingGeneral() throws Exception {
        final String projectName = "MethodParsingGeneral";

        final CILOSTAZOLLanguage lang = new CILOSTAZOLLanguage();
        CILOSTAZOLContext ctx = new CILOSTAZOLContext(lang, new Path[0]);
        Source source = getSourceFromProject(projectName);


        final IAppDomain domain = new AppDomain(ctx);
        final IAssembly assembly = Assembly.parse(source);
        domain.loadAssembly(assembly);

        final IComponent component= assembly.getComponents()[0];
        //Classes
        final IType classA= component.getLocalType("ComponentParsingGeneral","A");
        //final IType interfaceAI= component.getLocalType("ComponentParsingGeneral","AI");
        final IType classG_T= component.getLocalType("ComponentParsingGeneral","G`1");
        final IType classBar1= component.getLocalType("ComponentParsingGeneral","Bar1");
        final IType classBar2= component.getLocalType("ComponentParsingGeneral","Bar2`1");

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

    public void testComponentParsingGeneral() throws Exception {
        final String projectName = "ComponentParsingGeneral";

        CILOSTAZOLLanguage lang = new CILOSTAZOLLanguage();
        CILOSTAZOLContext ctx = new CILOSTAZOLContext(lang, new Path[0]);
        Source source = getSourceFromProject(projectName);

        IAppDomain domain = new AppDomain(ctx);
        IAssembly assembly = Assembly.parse(source);
        domain.loadAssembly(assembly);
    }

    public void testFindLocalType() throws Exception {
        final String projectName = "FindLocalType";

        CILOSTAZOLLanguage lang = new CILOSTAZOLLanguage();
        CILOSTAZOLContext ctx = new CILOSTAZOLContext(lang, new Path[0]);
        Source source = Source.newBuilder(
                        CILOSTAZOLLanguage.ID,
                        org.graalvm.polyglot.io.ByteSequence.create(Files.readAllBytes(getDllPath(projectName))), projectName)
                .build();


        IAppDomain domain = new AppDomain(ctx);
        IAssembly assembly = Assembly.parse(source);
        domain.loadAssembly(assembly);

        IType type = assembly.getLocalType("FindLocalType", "Class");

        assertEquals("FindLocalType", type.getNamespace());
        assertEquals("Class", type.getName());
    }

    public void testFindLocalType_Extends() throws Exception {
        final String projectName = "ExtendsTest";
        Source source = getSourceFromProject(projectName);

        CILOSTAZOLLanguage lang = new CILOSTAZOLLanguage();
        CILOSTAZOLContext ctx = new CILOSTAZOLContext(lang, new Path[0]);
        IAppDomain domain = new AppDomain(ctx);
        IAssembly assembly = Assembly.parse(source);
        domain.loadAssembly(assembly);
        IType type = assembly.getLocalType(projectName, "Class");


        assertEquals("ExtendsTest", type.getNamespace());
        assertEquals("Class", type.getName());

        assertEquals("AClass", type.getDirectBaseClass().getName());
        assertEquals("ExtendsTest", type.getDirectBaseClass().getNamespace());
    }


    public void testFindLocalType_Interfaces() throws Exception {
        final String projectName = "InterfacesTest";
        Source source = getSourceFromProject(projectName);

        CILOSTAZOLLanguage lang = new CILOSTAZOLLanguage();
        CILOSTAZOLContext ctx = new CILOSTAZOLContext(lang, new Path[0]);
        IAppDomain domain = new AppDomain(ctx);
        IAssembly assembly = Assembly.parse(source);
        domain.loadAssembly(assembly);
        IType type = assembly.getLocalType(projectName, "Class");


        assertEquals("InterfacesTest", type.getNamespace());
        assertEquals("Class", type.getName());

        assertEquals(2, type.getInterfaces().length);

        assertEquals("IClass", type.getInterfaces()[0].getName());
        assertEquals("InterfacesTest", type.getInterfaces()[0].getNamespace());

        assertEquals("IClass2", type.getInterfaces()[1].getName());
        assertEquals("InterfacesTest", type.getInterfaces()[1].getNamespace());
    }


    public void testFindLocalType_GenericTypeParams() throws Exception {
        final String projectName = "GenericTypeParametersTest";
        Source source = getSourceFromProject(projectName);

        CILOSTAZOLLanguage lang = new CILOSTAZOLLanguage();
        CILOSTAZOLContext ctx = new CILOSTAZOLContext(lang, new Path[0]);
        IAppDomain domain = new AppDomain(ctx);
        IAssembly assembly = Assembly.parse(source);
        domain.loadAssembly(assembly);
        IType type = assembly.getLocalType(projectName, "Class`1");


        assertEquals(1, type.getTypeParameters().length);
    }

    private Source getSourceFromProject(String projectName) throws IOException {
        return Source.newBuilder(
                        CILOSTAZOLLanguage.ID,
                        org.graalvm.polyglot.io.ByteSequence.create(Files.readAllBytes(getDllPath(projectName))), projectName)
                .build();
    }
}
