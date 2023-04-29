package com.vztekoverflow.cilostazol.runtime.typesystem;

import com.vztekoverflow.cilostazol.CILOSTAZOLLanguage;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.typesystem.assembly.Assembly;
import junit.framework.TestCase;
import org.graalvm.polyglot.Source;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

//You have to firstly build C# projects in test resources.
public class TypeParsingTest extends TestCase {
    public static final String _directory = "src/test/resources/TypeParsing";

    private Path getDllPath(String nameOfProj) {
        return Paths.get(_directory, nameOfProj,"bin/Release/net7.0", nameOfProj + ".dll");
    }

    public void testComponentParsing1() throws Exception {
        CILOSTAZOLLanguage lang = new CILOSTAZOLLanguage();
        CILOSTAZOLContext ctx = new CILOSTAZOLContext(lang, new Path[0]);
        Source source = Source.newBuilder(
                CILOSTAZOLLanguage.ID,
                org.graalvm.polyglot.io.ByteSequence.create(Files.readAllBytes(getDllPath("ComponentParsing1"))),
                "ComponentParsing1").build();


        AppDomain domain = new AppDomain(ctx);
        Assembly assembly = Assembly.parseAssembly(source, domain);
    }
}
