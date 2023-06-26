package com.vztekoverflow.cilostazol.runtime.typesystem;

import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.symbols.NamedTypeSymbol;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class PrimitiveTypesParsingTests extends TestBase {

  public static Stream<Arguments> provideNumericPrimitiveTypesData() {
    return Stream.of(
        Arguments.of("ByteClass", "Byte", "System"),
        Arguments.of("SByteClass", "SByte", "System"),
        Arguments.of("ShortClass", "Int16", "System"),
        Arguments.of("UShortClass", "UInt16", "System"),
        Arguments.of("IntClass", "Int32", "System"),
        Arguments.of("UIntClass", "UInt32", "System"),
        Arguments.of("LongClass", "Int64", "System"),
        Arguments.of("ULongClass", "UInt64", "System"),
        Arguments.of("FloatClass", "Single", "System"),
        Arguments.of("DoubleClass", "Double", "System")
        //                Arguments.of("DecimalClass", "Decimal", "System", "Decimal") //is not
        // supported -> issue #41
        );
  }

  public static Stream<Arguments> provideOtherPrimitiveTypesData() {
    return Stream.of(
        Arguments.of("CharClass", "Char", "System"),
        Arguments.of("BoolClass", "Boolean", "System"),
        Arguments.of("StringClass", "String", "System"),
        Arguments.of("ObjectClass", "Object", "System"));
  }

  public void testStructInheritance() {
    final String projectName = "PrimitiveTypesTest";
    CILOSTAZOLContext ctx =
        init(
            new Path[] {
              getDllPath(projectName).getParent(),
              Path.of("C:\\Users\\klepr\\source\\repos\\Softwerak\\Microsoft.NETCore.App\\6.0.10")
            });
    AssemblyIdentity assemblyIdentity = getAssemblyID(projectName);

    var type = ctx.getType("IntClass", projectName, assemblyIdentity);

    assertEquals(1, type.getFields().length);
    assertEquals("fieldInt", type.getFields()[0].getName());
    assertTrue(type.getFields()[0].getType() instanceof NamedTypeSymbol);

    NamedTypeSymbol int32 = (NamedTypeSymbol) type.getFields()[0].getType();
    NamedTypeSymbol valueType = int32.getDirectBaseClass();
    assertEquals("ValueType", valueType.getName());
    assertEquals("System", valueType.getNamespace());

    NamedTypeSymbol object = valueType.getDirectBaseClass();
    assertEquals("Object", object.getName());
    assertEquals("System", object.getNamespace());
  }

  public void testInt() {
    final String projectName = "PrimitiveTypesTest";
    CILOSTAZOLContext ctx =
        init(
            new Path[] {
              getDllPath(projectName).getParent(),
              Path.of("C:\\Users\\klepr\\source\\repos\\Softwerak\\Microsoft.NETCore.App\\6.0.10")
            });
    AssemblyIdentity assemblyIdentity = getAssemblyID(projectName);

    var type = ctx.getType("IntClass", projectName, assemblyIdentity);

    assertEquals(1, type.getFields().length);
    assertEquals("fieldInt", type.getFields()[0].getName());
    assertTrue(type.getFields()[0].getType() instanceof NamedTypeSymbol);

    NamedTypeSymbol int32 = (NamedTypeSymbol) type.getFields()[0].getType();
    assertEquals("Int32", int32.getName());
    assertEquals("System", int32.getNamespace());
    assertEquals(3, int32.getFields().length);
    assertTrue(Arrays.stream(int32.getFields()).anyMatch(f -> f.getName().equals("MinValue")));
    assertTrue(
        Arrays.stream(int32.getFields())
            .filter(f -> f.getName().equals("MinValue"))
            .findFirst()
            .get()
            .isStatic());
    assertTrue(Arrays.stream(int32.getFields()).anyMatch(f -> f.getName().equals("MaxValue")));
    assertTrue(
        Arrays.stream(int32.getFields())
            .filter(f -> f.getName().equals("MaxValue"))
            .findFirst()
            .get()
            .isStatic());

    NamedTypeSymbol valueType = int32.getDirectBaseClass();
    assertEquals("ValueType", valueType.getName());
    assertEquals("System", valueType.getNamespace());

    NamedTypeSymbol object = valueType.getDirectBaseClass();
    assertEquals("Object", object.getName());
    assertEquals("System", object.getNamespace());
  }

  public void testUInt() {
    final String projectName = "PrimitiveTypesTest";
    CILOSTAZOLContext ctx =
        init(
            new Path[] {
              getDllPath(projectName).getParent(),
              Path.of("C:\\Users\\klepr\\source\\repos\\Softwerak\\Microsoft.NETCore.App\\6.0.10")
            });
    AssemblyIdentity assemblyIdentity = getAssemblyID(projectName);

    var type = ctx.getType("UIntClass", projectName, assemblyIdentity);

    assertEquals(1, type.getFields().length);
    assertEquals("fieldUInt", type.getFields()[0].getName());
    assertTrue(type.getFields()[0].getType() instanceof NamedTypeSymbol);

    NamedTypeSymbol uint32 = (NamedTypeSymbol) type.getFields()[0].getType();
    assertEquals("UInt32", uint32.getName());
    assertEquals("System", uint32.getNamespace());
    assertEquals(3, uint32.getFields().length);
    assertTrue(Arrays.stream(uint32.getFields()).anyMatch(f -> f.getName().equals("MinValue")));
    assertTrue(
        Arrays.stream(uint32.getFields())
            .filter(f -> f.getName().equals("MinValue"))
            .findFirst()
            .get()
            .isStatic());
    assertTrue(Arrays.stream(uint32.getFields()).anyMatch(f -> f.getName().equals("MaxValue")));
    assertTrue(
        Arrays.stream(uint32.getFields())
            .filter(f -> f.getName().equals("MaxValue"))
            .findFirst()
            .get()
            .isStatic());
  }

  public void testVoid() {
    final String projectName = "PrimitiveTypesTest";
    CILOSTAZOLContext ctx =
        init(
            new Path[] {
              getDllPath(projectName).getParent(),
              Path.of("C:\\Users\\klepr\\source\\repos\\Softwerak\\Microsoft.NETCore.App\\6.0.10")
            });
    AssemblyIdentity assemblyIdentity = getAssemblyID(projectName);

    var type = ctx.getType("VoidClass", projectName, assemblyIdentity);

    assertEquals(1, type.getMethods().length);
    var method = type.getMethods()[0];
    assertEquals("Method", method.getName());
    assertTrue(method.getReturnType().getType() instanceof NamedTypeSymbol);

    NamedTypeSymbol voidType = (NamedTypeSymbol) method.getReturnType().getType();
    assertEquals("Void", voidType.getName());
    assertEquals("System", voidType.getNamespace());
  }

  @ParameterizedTest
  @MethodSource("provideNumericPrimitiveTypesData")
  public void testNumericPrimitiveTypes(
      String classname, String expectedTypeName, String expectedTypeNamespace) {
    final String projectName = "PrimitiveTypesTest";
    CILOSTAZOLContext ctx =
        init(
            new Path[] {
              getDllPath(projectName).getParent(),
              Path.of("C:\\Users\\klepr\\source\\repos\\Softwerak\\Microsoft.NETCore.App\\6.0.10")
            });
    AssemblyIdentity assemblyIdentity = getAssemblyID(projectName);

    var type = ctx.getType(classname, projectName, assemblyIdentity);

    assertEquals(1, type.getFields().length);
    assertTrue(type.getFields()[0].getType() instanceof NamedTypeSymbol);
    NamedTypeSymbol testType = (NamedTypeSymbol) type.getFields()[0].getType();

    assertEquals(expectedTypeName, testType.getName());
    assertEquals(expectedTypeNamespace, testType.getNamespace());
    assertTrue(Arrays.stream(testType.getFields()).anyMatch(f -> f.getName().equals("MinValue")));
    assertTrue(
        Arrays.stream(testType.getFields())
            .filter(f -> f.getName().equals("MinValue"))
            .findFirst()
            .get()
            .isStatic());
    assertTrue(Arrays.stream(testType.getFields()).anyMatch(f -> f.getName().equals("MaxValue")));
    assertTrue(
        Arrays.stream(testType.getFields())
            .filter(f -> f.getName().equals("MaxValue"))
            .findFirst()
            .get()
            .isStatic());
  }

  @ParameterizedTest
  @MethodSource("provideOtherPrimitiveTypesData")
  public void testOtherPrimitiveTypes(
      String classname, String expectedTypeName, String expectedTypeNamespace) {
    final String projectName = "PrimitiveTypesTest";
    CILOSTAZOLContext ctx =
        init(
            new Path[] {
              getDllPath(projectName).getParent(),
              Path.of("C:\\Users\\klepr\\source\\repos\\Softwerak\\Microsoft.NETCore.App\\6.0.10")
            });
    AssemblyIdentity assemblyIdentity = getAssemblyID(projectName);

    var type = ctx.getType(classname, projectName, assemblyIdentity);

    assertEquals(1, type.getFields().length);
    assertTrue(type.getFields()[0].getType() instanceof NamedTypeSymbol);
    NamedTypeSymbol testType = (NamedTypeSymbol) type.getFields()[0].getType();

    assertEquals(expectedTypeName, testType.getName());
    assertEquals(expectedTypeNamespace, testType.getNamespace());
  }
}
