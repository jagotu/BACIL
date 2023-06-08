package com.vztekoverflow.cilostazol.runtime.typesystem;

import com.vztekoverflow.cilostazol.CILOSTAZOLLanguage;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.typesystem.appdomain.AppDomain;
import com.vztekoverflow.cilostazol.runtime.typesystem.appdomain.IAppDomain;
import com.vztekoverflow.cilostazol.runtime.typesystem.assembly.Assembly;
import com.vztekoverflow.cilostazol.runtime.typesystem.assembly.IAssembly;
import com.vztekoverflow.cilostazol.runtime.typesystem.field.FieldVisibility;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;
import org.graalvm.polyglot.Source;

import java.nio.file.Path;
import java.util.Arrays;

public class FieldParsingTests extends TestBase {

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void testFieldParsing_General() throws Exception {
        final String projectName = "FieldTest";
        Source source = getSourceFromProject(projectName);

        CILOSTAZOLLanguage lang = new CILOSTAZOLLanguage();
        CILOSTAZOLContext ctx = new CILOSTAZOLContext(lang, new Path[0]);
        IAppDomain domain = new AppDomain(ctx);
        IAssembly assembly = Assembly.parse(domain, source);
        IType type = assembly.getLocalType(projectName, "Class");

        assertEquals(9, type.getFields().length);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void testFieldParsing_Types() throws Exception {
        final String projectName = "FieldTest";
        Source source = getSourceFromProject(projectName);

        CILOSTAZOLLanguage lang = new CILOSTAZOLLanguage();
        CILOSTAZOLContext ctx = new CILOSTAZOLContext(lang, new Path[0]);
        IAppDomain domain = new AppDomain(ctx);
        IAssembly assembly = Assembly.parse(domain, source);
        IType type = assembly.getLocalType(projectName, "Class");

        assertTrue(Arrays.stream(type.getFields()).anyMatch(f -> f.getName().equals("fieldClass2")));
        var fieldClass2Type = Arrays.stream(type.getFields()).filter(f -> f.getName().equals("fieldClass2")).findFirst().get().getType();
        assertEquals("Class1", fieldClass2Type.getName());
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void testFieldParsing_Staticness() throws Exception {
        final String projectName = "FieldTest";
        Source source = getSourceFromProject(projectName);

        CILOSTAZOLLanguage lang = new CILOSTAZOLLanguage();
        CILOSTAZOLContext ctx = new CILOSTAZOLContext(lang, new Path[0]);
        IAppDomain domain = new AppDomain(ctx);
        IAssembly assembly = Assembly.parse(domain, source);
        IType type = assembly.getLocalType(projectName, "Class");

        assertFalse(Arrays.stream(type.getFields()).filter(f -> f.getName().equals("fieldClass2")).findFirst().get().isStatic());
        assertTrue(Arrays.stream(type.getFields()).filter(f -> f.getName().equals("fieldStatic")).findFirst().get().isStatic());
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void testFieldParsing_Visibility() throws Exception {
        final String projectName = "FieldTest";
        Source source = getSourceFromProject(projectName);

        CILOSTAZOLLanguage lang = new CILOSTAZOLLanguage();
        CILOSTAZOLContext ctx = new CILOSTAZOLContext(lang, new Path[0]);
        IAppDomain domain = new AppDomain(ctx);
        IAssembly assembly = Assembly.parse(domain, source);
        IType type = assembly.getLocalType(projectName, "Class");

        assertTrue(Arrays.stream(type.getFields()).anyMatch(f -> f.getName().equals("fieldPrivate")));
        var fieldPrivate = Arrays.stream(type.getFields()).filter(f -> f.getName().equals("fieldPrivate")).findFirst().get();
        assertEquals(FieldVisibility.Private, fieldPrivate.getVisibility());

        assertTrue(Arrays.stream(type.getFields()).anyMatch(f -> f.getName().equals("fieldPublic")));
        var fieldPublic = Arrays.stream(type.getFields()).filter(f -> f.getName().equals("fieldPublic")).findFirst().get();
        assertEquals(FieldVisibility.Public, fieldPublic.getVisibility());

        assertTrue(Arrays.stream(type.getFields()).anyMatch(f -> f.getName().equals("fieldInternal")));
        var fieldInternal = Arrays.stream(type.getFields()).filter(f -> f.getName().equals("fieldInternal")).findFirst().get();
        assertEquals(FieldVisibility.Assembly, fieldInternal.getVisibility());

        assertTrue(Arrays.stream(type.getFields()).anyMatch(f -> f.getName().equals("fieldProtected")));
        var fieldProtected = Arrays.stream(type.getFields()).filter(f -> f.getName().equals("fieldProtected")).findFirst().get();
        assertEquals(FieldVisibility.Family, fieldProtected.getVisibility());

        assertTrue(Arrays.stream(type.getFields()).anyMatch(f -> f.getName().equals("fieldProtectedInternal")));
        var fieldProtectedInternal = Arrays.stream(type.getFields()).filter(f -> f.getName().equals("fieldProtectedInternal")).findFirst().get();
        assertEquals(FieldVisibility.FamORAssem, fieldProtectedInternal.getVisibility());

        assertTrue(Arrays.stream(type.getFields()).anyMatch(f -> f.getName().equals("fieldPrivateProtected")));
        var fieldPrivateProtected = Arrays.stream(type.getFields()).filter(f -> f.getName().equals("fieldPrivateProtected")).findFirst().get();
        assertEquals(FieldVisibility.FamANDAssem, fieldPrivateProtected.getVisibility());
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void testFieldParsing_SelfType() throws Exception {
        final String projectName = "FieldTest";
        Source source = getSourceFromProject(projectName);

        CILOSTAZOLLanguage lang = new CILOSTAZOLLanguage();
        CILOSTAZOLContext ctx = new CILOSTAZOLContext(lang, new Path[0]);
        IAppDomain domain = new AppDomain(ctx);
        IAssembly assembly = Assembly.parse(domain, source);
        IType type = assembly.getLocalType(projectName, "Class");

        assertTrue(Arrays.stream(type.getFields()).anyMatch(f -> f.getName().equals("fieldSelf")));
        var fieldSelf = Arrays.stream(type.getFields()).filter(f -> f.getName().equals("fieldSelf")).findFirst().get();
        var selfType = fieldSelf.getType();
        assertEquals(type.getName(), selfType.getName());
        assertEquals(type.getNamespace(), selfType.getNamespace());
    }

}
