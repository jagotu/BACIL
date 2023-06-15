package com.vztekoverflow.cilostazol.runtime.typesystem;

import com.vztekoverflow.cilostazol.CILOSTAZOLLanguage;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.typesystem.appdomain.AppDomain;
import com.vztekoverflow.cilostazol.runtime.typesystem.appdomain.IAppDomain;
import com.vztekoverflow.cilostazol.runtime.typesystem.assembly.Assembly;
import com.vztekoverflow.cilostazol.runtime.typesystem.assembly.IAssembly;
import com.vztekoverflow.cilostazol.runtime.typesystem.generic.Substitution;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.IMethod;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;
import java.nio.file.Path;
import java.util.HashMap;
import org.graalvm.polyglot.Source;

public class SubstitutionTest extends TestBase {
  private IAssembly getAssembly() throws Exception {
    final String projectName = "SubstitutionTest";

    final CILOSTAZOLLanguage lang = new CILOSTAZOLLanguage();
    CILOSTAZOLContext ctx = new CILOSTAZOLContext(lang, new Path[0]);
    Source source = getSourceFromProject(projectName);

    IAppDomain domain = new AppDomain(ctx);
    IAssembly assembly = Assembly.parse(domain, source);
    return assembly;
  }

  private IType getType(IAssembly assembly, String namespace, String klass) {
    return assembly.getLocalType(namespace, klass);
  }

  public void testSubTypeParameter() throws Exception {
    IAssembly assembly = getAssembly();
    // class Accessibility
    IType typeA1 = getType(assembly, "SubstitutionTest", "A1");
    IType typeG1 = getType(assembly, "SubstitutionTest", "G1a`1");

    Substitution<IType> substitution1 =
        new Substitution<>(
            new HashMap<IType, IType>() {
              {
                put(typeG1.getTypeParameters()[0], typeA1);
              }
            });

    IType substitutedG1 = typeG1.substitute(substitution1);
    assertEquals(typeA1, substitutedG1.getTypeParameters()[0]);
  }

  public void testSubInheritance() throws Exception {
    IAssembly assembly = getAssembly();
    IType typeA1 = getType(assembly, "SubstitutionTest", "A1");
    IType typeG1 = getType(assembly, "SubstitutionTest", "G1b`1");

    Substitution<IType> substitution1 =
        new Substitution<>(
            new HashMap<IType, IType>() {
              {
                put(typeG1.getTypeParameters()[0], typeA1);
              }
            });

    IType substitutedG1 = typeG1.substitute(substitution1);
    IType baseClass = substitutedG1.getDirectBaseClass();
    assertEquals(baseClass.getTypeParameters()[0], typeA1);
  }

  public void testSubFields() throws Exception {
    IAssembly assembly = getAssembly();
    IType typeA1 = getType(assembly, "SubstitutionTest", "A1");
    IType typeG1 = getType(assembly, "SubstitutionTest", "G1c`1");

    Substitution<IType> substitution1 =
        new Substitution<>(
            new HashMap<IType, IType>() {
              {
                put(typeG1.getTypeParameters()[0], typeA1);
              }
            });

    IType substitutedG1 = typeG1.substitute(substitution1);
    IType field = substitutedG1.getFields()[0].getType();
    assertEquals(typeA1, field);
  }

  public void testSubClassMethod() throws Exception {
    IAssembly assembly = getAssembly();
    IType typeA1 = getType(assembly, "SubstitutionTest", "A1");
    IType typeG1 = getType(assembly, "SubstitutionTest", "G1d`1");

    Substitution<IType> substitution1 =
        new Substitution<>(
            new HashMap<IType, IType>() {
              {
                put(typeG1.getTypeParameters()[0], typeA1);
              }
            });

    IType substitutedG1 = typeG1.substitute(substitution1);
    IMethod method = substitutedG1.getMethods()[0];
    assertEquals(typeA1, method.getParameters()[0].getType());
  }

  public void testNestedInheritance() throws Exception {
    IAssembly assembly = getAssembly();
    IType typeA1 = getType(assembly, "SubstitutionTest", "A1");
    IType typeG1 = getType(assembly, "SubstitutionTest", "G1e`1");

    Substitution<IType> substitution1 =
        new Substitution<>(
            new HashMap<IType, IType>() {
              {
                put(typeG1.getTypeParameters()[0], typeA1);
              }
            });

    IType substitutedG1 = typeG1.substitute(substitution1);
    IType typeParam =
        substitutedG1.getDirectBaseClass().getTypeParameters()[0].getTypeParameters()[0];
    assertEquals(typeA1, typeParam);
  }

  public void testMethodRetParam() throws Exception {
    IAssembly assembly = getAssembly();
    IType typeA1 = getType(assembly, "SubstitutionTest", "A1");
    IType typeMethod = getType(assembly, "SubstitutionTest", "Methods");
    IMethod method = typeMethod.getMethods()[2];

    Substitution<IType> substitution1 =
        new Substitution<>(
            new HashMap<IType, IType>() {
              {
                put(method.getTypeParameters()[0], typeA1);
              }
            });

    IMethod substitutedMethod = method.substitute(substitution1);
    assertEquals(typeA1, substitutedMethod.getReturnType().getType());
  }

  public void testMethodSubLocals() throws Exception {
    IAssembly assembly = getAssembly();
    IType typeA1 = getType(assembly, "SubstitutionTest", "A1");
    IType typeMethod = getType(assembly, "SubstitutionTest", "Methods");
    IMethod method = typeMethod.getMethods()[3];

    Substitution<IType> substitution1 =
        new Substitution<>(
            new HashMap<IType, IType>() {
              {
                put(method.getTypeParameters()[0], typeA1);
              }
            });

    IMethod substitutedMethod = method.substitute(substitution1);
    assertEquals(typeA1, substitutedMethod.getLocals()[0].getType());
  }

  public void testMethodSubTryBlock() throws Exception {
    IAssembly assembly = getAssembly();
    IType typeA1 = getType(assembly, "SubstitutionTest", "A1");
    IType typeMethod = getType(assembly, "SubstitutionTest", "Methods");
    IMethod method = typeMethod.getMethods()[4];

    Substitution<IType> substitution1 =
        new Substitution<>(
            new HashMap<IType, IType>() {
              {
                put(method.getTypeParameters()[0], typeA1);
              }
            });

    IMethod substitutedMethod = method.substitute(substitution1);
    assertEquals(typeA1, substitutedMethod.getExceptionHandlers()[0].getHandlingException());
  }

  public void testMethodSubContraint() throws Exception {
    IAssembly assembly = getAssembly();
    IType typeA1 = getType(assembly, "SubstitutionTest", "A1");
    IType typeMethod = getType(assembly, "SubstitutionTest", "Methods");
    IMethod method = typeMethod.getMethods()[5];

    Substitution<IType> substitution1 =
        new Substitution<>(
            new HashMap<IType, IType>() {
              {
                put(method.getTypeParameters()[0], typeA1);
              }
            });

    IMethod substitutedMethod = method.substitute(substitution1);
    // assertEquals(typeA1, substitutedMethod.getTypeParameters()[0]);
  }

  public void testSubInterfaceImpl() throws Exception {}
}
