package com.vztekoverflow.cilostazol.runtime.typesystem;

import junit.framework.TestCase;
import org.graalvm.polyglot.io.ByteSequence;

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
        byte[] data = Files.readAllBytes(getDllPath("ComponentParsing1"));
        ByteSequence bytes = ByteSequence.create(data);
        Component c = CILComponent.parse(bytes);
        //assert component
    }
}
