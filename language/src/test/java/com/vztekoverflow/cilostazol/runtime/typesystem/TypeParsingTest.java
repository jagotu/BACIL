package com.vztekoverflow.cilostazol.runtime.typesystem;

import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cilostazol.CILOSTAZOLLanguage;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.symbols.NamedTypeSymbol;
import java.nio.file.Path;
import org.graalvm.polyglot.Source;

/**
 * You have to first build C# projects in test resources: {@value _directory}.
 *
 * <p>Build it with configuration: {@value _directory} and .NET version: {@value _dotnetVersion}
 */
public class TypeParsingTest extends TestBase {

  public void testNewStructure() {
    final String projectName = "ComponentParsingGeneral";
    CILOSTAZOLLanguage lang = new CILOSTAZOLLanguage();
    CILOSTAZOLContext ctx =
        new CILOSTAZOLContext(lang, new Path[] {getDllPath(projectName).getParent()});
    AssemblyIdentity assemblyIdentity =
        new AssemblyIdentity((short) 1, (short) 0, (short) 0, (short) 0, "ComponentParsingGeneral");

    NamedTypeSymbol type = ctx.getType("FindLocalType", "Class", assemblyIdentity);

    // No error thrown
  }

  public void testComponentParsingGeneral() throws Exception {
    final String projectName = "ComponentParsingGeneral";
    CILOSTAZOLContext ctx = new CILOSTAZOLContext(new CILOSTAZOLLanguage(), new Path[0]);
    Source source = getSourceFromProject(projectName);

    var assembly = ctx.loadAssembly(source);
    // No error thrown
  }

  public void testFindLocalType() throws Exception {
    final String projectName = "FindLocalType";
    CILOSTAZOLContext ctx = new CILOSTAZOLContext(new CILOSTAZOLLanguage(), new Path[0]);
    var assembly = ctx.loadAssembly(getSourceFromProject(projectName));

    var type = assembly.getLocalType("Class", "FindLocalType");

    assertEquals("FindLocalType", type.getNamespace());
    assertEquals("Class", type.getName());
  }

  public void testFindLocalType_Extends() throws Exception {
    final String projectName = "ExtendsTest";
    CILOSTAZOLContext ctx = new CILOSTAZOLContext(new CILOSTAZOLLanguage(), new Path[0]);
    var assembly = ctx.loadAssembly(getSourceFromProject(projectName));

    var type = assembly.getLocalType("Class", projectName);

    assertEquals("ExtendsTest", type.getNamespace());
    assertEquals("Class", type.getName());

    assertEquals("AClass", ((NamedTypeSymbol) type.getDirectBaseClass()).getName());
    assertEquals("ExtendsTest", ((NamedTypeSymbol) type.getDirectBaseClass()).getNamespace());
  }

  public void testFindLocalType_Interfaces() throws Exception {
    final String projectName = "InterfacesTest";
    CILOSTAZOLContext ctx = new CILOSTAZOLContext(new CILOSTAZOLLanguage(), new Path[0]);
    var assembly = ctx.loadAssembly(getSourceFromProject(projectName));

    var type = assembly.getLocalType("Class", projectName);

    assertEquals("InterfacesTest", type.getNamespace());
    assertEquals("Class", type.getName());
    assertEquals(2, type.getInterfaces().length);

    var interface1 = (NamedTypeSymbol) type.getInterfaces()[0];
    assertEquals("IClass", interface1.getName());
    assertEquals("InterfacesTest", interface1.getNamespace());
    assertTrue(interface1.isInterface());
    assertFalse(interface1.isClass());

    var interface2 = (NamedTypeSymbol) type.getInterfaces()[1];
    assertEquals("IClass2", interface2.getName());
    assertEquals("InterfacesTest", interface2.getNamespace());
    assertTrue(interface2.isInterface());
    assertFalse(interface2.isClass());
  }

  public void testFindLocalType_GenericTypeParams() throws Exception {
    final String projectName = "GenericTypeParametersTest";
    Source source = getSourceFromProject(projectName);
    CILOSTAZOLContext ctx = new CILOSTAZOLContext(new CILOSTAZOLLanguage(), new Path[0]);
    var assembly = ctx.loadAssembly(getSourceFromProject(projectName));

    var type = assembly.getLocalType("Class`1", projectName);

    assertEquals(1, type.getTypeParameters().length);
  }

  public void _testFindNonLocalType_OtherModule() throws Exception {
    final String projectName = "FindNonLocalType";
    CILOSTAZOLContext ctx = new CILOSTAZOLContext(new CILOSTAZOLLanguage(), new Path[0]);
    var assembly = ctx.loadAssembly(getSourceFromProject(projectName));

    var localType = assembly.getLocalType("Class1", projectName);

    assertEquals(2, localType.getFields().length);
    assertEquals("Class2", localType.getFields()[1].getType().getName());
    assertEquals("FindNonLocalType", localType.getFields()[1].getType().getNamespace());
  }

  public void testFindNonLocalType_OtherAssembly() throws Exception {
    final String projectName = "FindNonLocalType";
    final String otherProjectName = "FindLocalType";

    CILOSTAZOLContext ctx = new CILOSTAZOLContext(new CILOSTAZOLLanguage(), new Path[0]);
    var assembly = ctx.loadAssembly(getSourceFromProject(projectName));
    var otherAssembly = ctx.loadAssembly(getSourceFromProject(otherProjectName));

    var localType = assembly.getLocalType("Class1", projectName);

    assertEquals(2, localType.getFields().length);
    assertEquals("Class", localType.getFields()[0].getType().getName());
    assertEquals("FindLocalType", localType.getFields()[0].getType().getNamespace());
  }
}
