package com.vztekoverflow.cilostazol.runtime.typesystem;

import com.vztekoverflow.cilostazol.CILOSTAZOLLanguage;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.typesystem.appdomain.AppDomain;
import com.vztekoverflow.cilostazol.runtime.typesystem.appdomain.IAppDomain;
import com.vztekoverflow.cilostazol.runtime.typesystem.assembly.Assembly;
import com.vztekoverflow.cilostazol.runtime.typesystem.assembly.IAssembly;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.TypeBase;
import org.graalvm.polyglot.Source;

import java.nio.file.Files;
import java.nio.file.Path;


/**
 * You have to first build C# projects in test resources:  {@value  _directory}.
 * <p>
 * Build it with configuration: {@value _directory} and .NET version: {@value _dotnetVersion}
 */
public class TypeParsingTest extends TestBase {
    public void testComponentParsingGeneral() throws Exception {
        final String projectName = "ComponentParsingGeneral";

        CILOSTAZOLLanguage lang = new CILOSTAZOLLanguage();
        CILOSTAZOLContext ctx = new CILOSTAZOLContext(lang, new Path[0]);
        Source source = getSourceFromProject(projectName);

        IAppDomain domain = new AppDomain(ctx);
        IAssembly assembly = Assembly.parse(source);
        domain.loadAssembly(assembly);

        assertFalse(domain.getAssemblies().length == 0);
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

        var interface1 = (TypeBase) type.getInterfaces()[0];
        assertEquals("IClass", interface1.getName());
        assertEquals("InterfacesTest", interface1.getNamespace());
        assertTrue(interface1.isInterface());
        assertFalse(interface1.isClass());

        var interface2 = (TypeBase) type.getInterfaces()[1];
        assertEquals("IClass2", interface2.getName());
        assertEquals("InterfacesTest", interface2.getNamespace());
        assertTrue(interface2.isInterface());
        assertFalse(interface2.isClass());
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
}
