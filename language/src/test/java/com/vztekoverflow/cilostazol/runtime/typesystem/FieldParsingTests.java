package com.vztekoverflow.cilostazol.runtime.typesystem;

import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.symbols.NamedTypeSymbol;
import com.vztekoverflow.cilostazol.runtime.symbols.utils.FieldSymbolVisibility;
import java.nio.file.Path;
import java.util.Arrays;

public class FieldParsingTests extends TestBase {

  public void testFieldParsing_General() {
    final String projectName = "FieldTest";
    CILOSTAZOLContext ctx = init(new Path[] {getDllPath(projectName).getParent()});
    AssemblyIdentity assemblyIdentity = getAssemblyID(projectName);

    var type = ctx.getType("Class", projectName, assemblyIdentity);

    assertEquals(9, type.getFields().length);
  }

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  public void testFieldParsing_Types() {
    final String projectName = "FieldTest";
    CILOSTAZOLContext ctx = init(new Path[] {getDllPath(projectName).getParent()});
    AssemblyIdentity assemblyIdentity = getAssemblyID(projectName);

    var type = ctx.getType("Class", projectName, assemblyIdentity);

    assertTrue(Arrays.stream(type.getFields()).anyMatch(f -> f.getName().equals("fieldClass2")));
    var fieldClass2Type =
        Arrays.stream(type.getFields())
            .filter(f -> f.getName().equals("fieldClass2"))
            .findFirst()
            .get()
            .getType();

    assertTrue(fieldClass2Type instanceof NamedTypeSymbol);
    assertEquals("Class1", ((NamedTypeSymbol)fieldClass2Type).getName());
  }

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  public void testFieldParsing_Staticness() {
    final String projectName = "FieldTest";
    CILOSTAZOLContext ctx = init(new Path[] {getDllPath(projectName).getParent()});
    AssemblyIdentity assemblyIdentity = getAssemblyID(projectName);

    var type = ctx.getType("Class", projectName, assemblyIdentity);

    assertFalse(
        Arrays.stream(type.getFields())
            .filter(f -> f.getName().equals("fieldClass2"))
            .findFirst()
            .get()
            .isStatic());
    assertTrue(
        Arrays.stream(type.getFields())
            .filter(f -> f.getName().equals("fieldStatic"))
            .findFirst()
            .get()
            .isStatic());
  }

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  public void testFieldParsing_Visibility() {
    final String projectName = "FieldTest";
    CILOSTAZOLContext ctx = init(new Path[] {getDllPath(projectName).getParent()});
    AssemblyIdentity assemblyIdentity = getAssemblyID(projectName);

    var type = ctx.getType("Class", projectName, assemblyIdentity);

    assertTrue(Arrays.stream(type.getFields()).anyMatch(f -> f.getName().equals("fieldPrivate")));
    var fieldPrivate =
        Arrays.stream(type.getFields())
            .filter(f -> f.getName().equals("fieldPrivate"))
            .findFirst()
            .get();
    assertEquals(FieldSymbolVisibility.Private, fieldPrivate.getVisibility());

    assertTrue(Arrays.stream(type.getFields()).anyMatch(f -> f.getName().equals("fieldPublic")));
    var fieldPublic =
        Arrays.stream(type.getFields())
            .filter(f -> f.getName().equals("fieldPublic"))
            .findFirst()
            .get();
    assertEquals(FieldSymbolVisibility.Public, fieldPublic.getVisibility());

    assertTrue(Arrays.stream(type.getFields()).anyMatch(f -> f.getName().equals("fieldInternal")));
    var fieldInternal =
        Arrays.stream(type.getFields())
            .filter(f -> f.getName().equals("fieldInternal"))
            .findFirst()
            .get();
    assertEquals(FieldSymbolVisibility.Assembly, fieldInternal.getVisibility());

    assertTrue(Arrays.stream(type.getFields()).anyMatch(f -> f.getName().equals("fieldProtected")));
    var fieldProtected =
        Arrays.stream(type.getFields())
            .filter(f -> f.getName().equals("fieldProtected"))
            .findFirst()
            .get();
    assertEquals(FieldSymbolVisibility.Family, fieldProtected.getVisibility());

    assertTrue(
        Arrays.stream(type.getFields())
            .anyMatch(f -> f.getName().equals("fieldProtectedInternal")));
    var fieldProtectedInternal =
        Arrays.stream(type.getFields())
            .filter(f -> f.getName().equals("fieldProtectedInternal"))
            .findFirst()
            .get();
    assertEquals(FieldSymbolVisibility.FamORAssem, fieldProtectedInternal.getVisibility());

    assertTrue(
        Arrays.stream(type.getFields()).anyMatch(f -> f.getName().equals("fieldPrivateProtected")));
    var fieldPrivateProtected =
        Arrays.stream(type.getFields())
            .filter(f -> f.getName().equals("fieldPrivateProtected"))
            .findFirst()
            .get();
    assertEquals(FieldSymbolVisibility.FamANDAssem, fieldPrivateProtected.getVisibility());
  }

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  public void testFieldParsing_SelfType() throws Exception {
    final String projectName = "FieldTest";
    CILOSTAZOLContext ctx = init(new Path[] {getDllPath(projectName).getParent()});
    AssemblyIdentity assemblyIdentity = getAssemblyID(projectName);

    var type = ctx.getType("Class", projectName, assemblyIdentity);

    assertTrue(Arrays.stream(type.getFields()).anyMatch(f -> f.getName().equals("fieldSelf")));
    var fieldSelf =
        Arrays.stream(type.getFields())
            .filter(f -> f.getName().equals("fieldSelf"))
            .findFirst()
            .get();
    var selfType = fieldSelf.getType();
    assertTrue(selfType instanceof NamedTypeSymbol);
    assertEquals(type.getName(), ((NamedTypeSymbol)selfType).getName());
    assertEquals(type.getNamespace(), ((NamedTypeSymbol)selfType).getNamespace());
  }
}
