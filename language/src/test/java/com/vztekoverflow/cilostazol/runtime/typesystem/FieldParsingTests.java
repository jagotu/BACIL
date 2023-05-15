package com.vztekoverflow.cilostazol.runtime.typesystem;

import com.vztekoverflow.cilostazol.CILOSTAZOLLanguage;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.typesystem.appdomain.AppDomain;
import com.vztekoverflow.cilostazol.runtime.typesystem.appdomain.IAppDomain;
import com.vztekoverflow.cilostazol.runtime.typesystem.assembly.Assembly;
import com.vztekoverflow.cilostazol.runtime.typesystem.assembly.IAssembly;
import com.vztekoverflow.cilostazol.runtime.typesystem.type.IType;
import org.graalvm.polyglot.Source;

import java.nio.file.Path;
import java.util.Arrays;

public class FieldParsingTests extends TestBase {
    public void testFieldParsingGeneral() throws Exception {
        final String projectName = "FieldTest";
        Source source = getSourceFromProject(projectName);

        CILOSTAZOLLanguage lang = new CILOSTAZOLLanguage();
        CILOSTAZOLContext ctx = new CILOSTAZOLContext(lang, new Path[0]);
        IAppDomain domain = new AppDomain(ctx);
        IAssembly assembly = Assembly.parse(source);
        domain.loadAssembly(assembly);
        IType type = assembly.getLocalType(projectName, "Class");

        //TODO: test field types
        assertEquals(7, type.getFields().length);
        assertTrue(Arrays.stream(type.getFields()).anyMatch(f -> f.getName().equals("fieldPrivate")));
        assertTrue(Arrays.stream(type.getFields()).anyMatch(f -> f.getName().equals("fieldPublic")));
        assertTrue(Arrays.stream(type.getFields()).anyMatch(f -> f.getName().equals("fieldInternal")));
        assertTrue(Arrays.stream(type.getFields()).anyMatch(f -> f.getName().equals("fieldProtected")));
        assertTrue(Arrays.stream(type.getFields()).anyMatch(f -> f.getName().equals("fieldSelf")));
        assertTrue(Arrays.stream(type.getFields()).anyMatch(f -> f.getName().equals("fieldClass2")));
        //noinspection OptionalGetWithoutIsPresent
        assertFalse(Arrays.stream(type.getFields()).filter(f -> f.getName().equals("fieldClass2")).findFirst().get().isStatic());
        assertTrue(Arrays.stream(type.getFields()).anyMatch(f -> f.getName().equals("fieldStatic")));
        //noinspection OptionalGetWithoutIsPresent
        assertTrue(Arrays.stream(type.getFields()).filter(f -> f.getName().equals("fieldStatic")).findFirst().get().isStatic());
    }
}
