package com.vztekoverflow.cilostazol.runtime.typesystem;

import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cilostazol.CILOSTAZOLLanguage;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.symbols.AssemblySymbol;
import com.vztekoverflow.cilostazol.runtime.symbols.NamedTypeSymbol;
import com.vztekoverflow.cilostazol.runtime.typesystem.appdomain.AppDomain;
import com.vztekoverflow.cilostazol.runtime.typesystem.appdomain.IAppDomain;
import com.vztekoverflow.cilostazol.runtime.typesystem.assembly.Assembly;
import com.vztekoverflow.cilostazol.runtime.typesystem.assembly.IAssembly;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.TypeBase;
import java.nio.file.Files;
import java.nio.file.Path;
import org.graalvm.polyglot.Source;

/**
 * You have to first build C# projects in test resources: {@value _directory}.
 *
 * <p>Build it with configuration: {@value _directory} and .NET version: {@value _dotnetVersion}
 */
public class TypeParsingTest extends TestBase {
  public void testNewStructure() throws Exception {
    final String projectName = "ComponentParsingGeneral";
    CILOSTAZOLLanguage lang = new CILOSTAZOLLanguage();
    CILOSTAZOLContext ctx = new CILOSTAZOLContext(lang, new Path[] { getDllPath(projectName).getParent() } );
    AssemblyIdentity assemblyIdentity = new AssemblyIdentity((short) 1,(short) 0,(short) 0,(short) 0, "ComponentParsingGeneral");

    NamedTypeSymbol type = ctx.getType("FindLocalType", "Class", assemblyIdentity);
  }
  public void testComponentParsingGeneral() throws Exception {
    final String projectName = "ComponentParsingGeneral";

    CILOSTAZOLLanguage lang = new CILOSTAZOLLanguage();
    CILOSTAZOLContext ctx = new CILOSTAZOLContext(lang, new Path[0]);
    Source source = getSourceFromProject(projectName);

    IAppDomain domain = new AppDomain(ctx);
    IAssembly assembly = Assembly.parse(domain, source);

    assertFalse(domain.getAssemblies().length == 0);
  }

  public void testFindLocalType() throws Exception {
    final String projectName = "FindLocalType";

    CILOSTAZOLLanguage lang = new CILOSTAZOLLanguage();
    CILOSTAZOLContext ctx = new CILOSTAZOLContext(lang, new Path[0]);
    Source source =
        Source.newBuilder(
                CILOSTAZOLLanguage.ID,
                org.graalvm.polyglot.io.ByteSequence.create(
                    Files.readAllBytes(getDllPath(projectName))),
                projectName)
            .build();

    IAppDomain domain = new AppDomain(ctx);
    IAssembly assembly = Assembly.parse(domain, source);

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
    IAssembly assembly = Assembly.parse(domain, source);
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
    IAssembly assembly = Assembly.parse(domain, source);
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
    IAssembly assembly = Assembly.parse(domain, source);
    IType type = assembly.getLocalType(projectName, "Class`1");

    assertEquals(1, type.getTypeParameters().length);
  }

  public void testFindNonLocalType_OtherModule() throws Exception {
    final String projectName = "FindNonLocalType";
    Source source = getSourceFromProject(projectName);
    Source[] sources = getSourcesFromProjects();

    CILOSTAZOLLanguage lang = new CILOSTAZOLLanguage();
    CILOSTAZOLContext ctx = new CILOSTAZOLContext(lang, new Path[0]);
    IAppDomain domain = new AppDomain(ctx);
    IAssembly assembly = Assembly.parse(domain, source);
    IType localType = assembly.getLocalType(projectName, "Class1");

    assertEquals(2, localType.getFields().length);
    assertEquals("Class2", localType.getFields()[1].getType().getName());
    assertEquals("FindNonLocalType", localType.getFields()[1].getType().getNamespace());
  }

  public void testFindNonLocalType_OtherAssembly() throws Exception {
    final String projectName = "FindNonLocalType";
    final String otherProjectName = "FindLocalType";
    Source[] sources = getSourcesFromProjects(projectName, otherProjectName);

    CILOSTAZOLLanguage lang = new CILOSTAZOLLanguage();
    CILOSTAZOLContext ctx = new CILOSTAZOLContext(lang, new Path[0]);
    IAppDomain domain = new AppDomain(ctx);
    IAssembly assembly = Assembly.parse(domain, sources[0]);
    // foreign assembly
    // TODO: this should be done by assembly loader?
    IAssembly otherAssembly = Assembly.parse(domain, sources[1]);

    IType localType = assembly.getLocalType(projectName, "Class1");

    assertEquals(2, localType.getFields().length);
    assertEquals("Class", localType.getFields()[0].getType().getName());
    assertEquals("FindLocalType", localType.getFields()[0].getType().getNamespace());
  }
}
