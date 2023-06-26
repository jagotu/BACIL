package com.vztekoverflow.cilostazol.runtime.typesystem;

import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.symbols.*;
import java.nio.file.Path;

public class SubstitutionTest extends TestBase {
  public void testSubTypeParameter() {
    final String projectName = "SubstitutionTest";
    final CILOSTAZOLContext ctx = init(new Path[] {getDllPath(projectName).getParent()});
    final AssemblyIdentity assemblyID = getAssemblyID(projectName);

    // class Accessibility
    NamedTypeSymbol typeA1 = ctx.getType("A1", "SubstitutionTest", assemblyID);
    NamedTypeSymbol typeG1 = ctx.getType("G1a`1", "SubstitutionTest", assemblyID);

    ConstructedNamedTypeSymbol substitutedG1 = typeG1.construct(new TypeSymbol[] {typeA1});
    assertEquals(typeA1, substitutedG1.getTypeArguments()[0]);
  }

  public void testSubInheritance() {
    final String projectName = "SubstitutionTest";
    final CILOSTAZOLContext ctx = init(new Path[] {getDllPath(projectName).getParent()});
    final AssemblyIdentity assemblyID = getAssemblyID(projectName);

    TypeSymbol typeA1 = ctx.getType("A1", "SubstitutionTest", assemblyID);
    NamedTypeSymbol typeG1 = ctx.getType("G1b`1", "SubstitutionTest", assemblyID);

    ConstructedNamedTypeSymbol substitutedG1 = typeG1.construct(new TypeSymbol[] {typeA1});
    NamedTypeSymbol baseClass = substitutedG1.getDirectBaseClass();
    assertEquals(baseClass.getTypeArguments()[0], typeA1);
  }

  public void testSubFields() {
    final String projectName = "SubstitutionTest";
    final CILOSTAZOLContext ctx = init(new Path[] {getDllPath(projectName).getParent()});
    final AssemblyIdentity assemblyID = getAssemblyID(projectName);

    NamedTypeSymbol typeA1 = ctx.getType("A1", "SubstitutionTest", assemblyID);
    NamedTypeSymbol typeG1 = ctx.getType("G1c`1", "SubstitutionTest", assemblyID);

    NamedTypeSymbol substitutedG1 = typeG1.construct(new TypeSymbol[] {typeA1});
    TypeSymbol field = substitutedG1.getFields()[0].getType();
    assertEquals(typeA1, field);
  }

  public void testSubClassMethod() {
    final String projectName = "SubstitutionTest";
    final CILOSTAZOLContext ctx = init(new Path[] {getDllPath(projectName).getParent()});
    final AssemblyIdentity assemblyID = getAssemblyID(projectName);

    NamedTypeSymbol typeA1 = ctx.getType("A1", "SubstitutionTest", assemblyID);
    NamedTypeSymbol typeG1 = ctx.getType("G1d`1", "SubstitutionTest", assemblyID);

    NamedTypeSymbol substitutedG1 = typeG1.construct(new TypeSymbol[] {typeA1});
    MethodSymbol method = substitutedG1.getMethods()[0];
    assertEquals(typeA1, method.getParameters()[0].getType());
  }

  public void testNestedInheritance() {
    final String projectName = "SubstitutionTest";
    final CILOSTAZOLContext ctx = init(new Path[] {getDllPath(projectName).getParent()});
    final AssemblyIdentity assemblyID = getAssemblyID(projectName);

    NamedTypeSymbol typeA1 = ctx.getType("A1", "SubstitutionTest", assemblyID);
    NamedTypeSymbol typeG1 = ctx.getType("G1e`1", "SubstitutionTest", assemblyID);

    NamedTypeSymbol substitutedG1 = typeG1.construct(new TypeSymbol[] {typeA1});
    TypeSymbol typeParam =
        ((NamedTypeSymbol) (substitutedG1.getDirectBaseClass().getTypeArguments()[0]))
            .getTypeArguments()[0];
    assertEquals(typeA1, typeParam);
  }

  public void testMethodRetParam() {
    final String projectName = "SubstitutionTest";
    final CILOSTAZOLContext ctx = init(new Path[] {getDllPath(projectName).getParent()});
    final AssemblyIdentity assemblyID = getAssemblyID(projectName);

    NamedTypeSymbol typeA1 = ctx.getType("A1", "SubstitutionTest", assemblyID);
    NamedTypeSymbol typeMethod = ctx.getType("Methods", "SubstitutionTest", assemblyID);
    MethodSymbol method = typeMethod.getMethods()[2];

    MethodSymbol substitutedMethod = method.construct(new TypeSymbol[] {typeA1});
    assertEquals(typeA1, substitutedMethod.getReturnType().getType());
  }

  public void testMethodSubLocals() {
    final String projectName = "SubstitutionTest";
    final CILOSTAZOLContext ctx = init(new Path[] {getDllPath(projectName).getParent()});
    final AssemblyIdentity assemblyID = getAssemblyID(projectName);

    NamedTypeSymbol typeA1 = ctx.getType("A1", "SubstitutionTest", assemblyID);
    NamedTypeSymbol typeMethod = ctx.getType("Methods", "SubstitutionTest", assemblyID);
    MethodSymbol method = typeMethod.getMethods()[3];

    MethodSymbol substitutedMethod = method.construct(new TypeSymbol[] {typeA1});
    assertEquals(typeA1, substitutedMethod.getLocals()[0].getType());
  }

  public void testMethodSubTryBlock() {
    final String projectName = "SubstitutionTest";
    final CILOSTAZOLContext ctx = init(new Path[] {getDllPath(projectName).getParent()});
    final AssemblyIdentity assemblyID = getAssemblyID(projectName);

    NamedTypeSymbol typeA1 = ctx.getType("A1", "SubstitutionTest", assemblyID);
    NamedTypeSymbol typeMethod = ctx.getType("Methods", "SubstitutionTest", assemblyID);
    MethodSymbol method = typeMethod.getMethods()[4];

    MethodSymbol substitutedMethod = method.construct(new TypeSymbol[] {typeA1});
    assertEquals(typeA1, substitutedMethod.getExceptionHandlers()[0].getHandlerException());
  }

  public void testMethodSubContraint() {
    final String projectName = "SubstitutionTest";
    final CILOSTAZOLContext ctx = init(new Path[] {getDllPath(projectName).getParent()});
    final AssemblyIdentity assemblyID = getAssemblyID(projectName);

    NamedTypeSymbol typeA1 = ctx.getType("A1", "SubstitutionTest", assemblyID);
    NamedTypeSymbol typeMethod = ctx.getType("Methods", "SubstitutionTest", assemblyID);
    MethodSymbol method = typeMethod.getMethods()[5];

    MethodSymbol substitutedMethod = method.construct(new TypeSymbol[] {typeA1});
    assertEquals(
        typeA1,
        ((NamedTypeSymbol) substitutedMethod.getTypeParameters()[0].getTypeConstrains()[0])
            .getTypeArguments()[0]);
  }

  public void testSubInterfaceImpl() {
    final String projectName = "SubstitutionTest";
    final CILOSTAZOLContext ctx = init(new Path[] {getDllPath(projectName).getParent()});
    final AssemblyIdentity assemblyID = getAssemblyID(projectName);

    NamedTypeSymbol typeA1 = ctx.getType("A1", "SubstitutionTest", assemblyID);
    NamedTypeSymbol typeG2a = ctx.getType("G2a`2", "SubstitutionTest", assemblyID);

    NamedTypeSymbol substitutedTypeG2a = typeG2a.construct(new TypeSymbol[] {typeA1, typeA1});
    assertEquals(typeA1, substitutedTypeG2a.getInterfaces()[0].getTypeArguments()[0]);
    assertEquals(typeA1, substitutedTypeG2a.getInterfaces()[1].getTypeArguments()[0]);
  }
}
