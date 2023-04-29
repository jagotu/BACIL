package com.vztekoverflow.cilostazol.runtime.typesystem;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.vztekoverflow.cilostazol.CILOSTAZOLLanguage;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.typesystem.assembly.Assembly;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.CILComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.Component;
import junit.framework.TestCase;
import org.graalvm.options.OptionDescriptors;
import org.graalvm.options.OptionKey;
import org.graalvm.options.OptionValues;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Language;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.io.ByteSequence;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

//You have to firstly build C# projects in test resources.
public class TypeParsingTest extends TestCase {
    public static final String _directory = "src/test/resources/TypeParsing";

    private Path getDllPath(String nameOfProj) {
        return Paths.get(_directory, nameOfProj,"bin/Release/net7.0", nameOfProj + ".dll");
    }

    public void testComponentParsing1() throws Exception {
        CILOSTAZOLLanguage lang = new CILOSTAZOLLanguage();
        CILOSTAZOLContext ctx = new CILOSTAZOLContext(lang, new Path[0]);
        Source source = Source.newBuilder(CILOSTAZOLLanguage.ID, getDllPath("ComponentParsing1").toFile()).build();


        AppDomain domain = new AppDomain(ctx);
        Assembly assembly = Assembly.parseAssembly(source);
    }
}
