package com.vztekoverflow.cilostazol.runtime.typesystem;

import com.oracle.truffle.api.nodes.RootNode;
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
import com.vztekoverflow.cilostazol.runtime.typesystem.type.factory.TypeFactory;
import junit.framework.TestCase;
import org.graalvm.polyglot.Source;

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
        final IType program = component.getLocalType("ComponentParsingGeneral","Class");

        final CLIMethodDefTableRow mainDef = assembly.getDefiningFile().getTableHeads().getMethodDefTableHead().skip(5);
        final IMethod main = MethodFactory.create(mainDef, program);
        RootNode node = main.getNode();
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

    public void _testFindLocalType() throws Exception {
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
        assertEquals(1, type.getInterfaces().length);
        assertEquals("IClass", type.getInterfaces()[0].getName());
        assertEquals("FindLocalType", type.getInterfaces()[0].getNamespace());
    }

    public void testFindLocalType_Extends() throws Exception {
        final String projectName = "ExtendsTest";
        Source source = getSourceFromProject(projectName);


        IAppDomain domain = new AppDomain();
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


        IAppDomain domain = new AppDomain();
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

    private Source getSourceFromProject(String projectName) throws IOException {
        return Source.newBuilder(
                        CILOSTAZOLLanguage.ID,
                        org.graalvm.polyglot.io.ByteSequence.create(Files.readAllBytes(getDllPath(projectName))), projectName)
                .build();
    }
}
