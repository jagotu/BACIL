package com.vztekoverflow.cilostazol.runtime.typesystem;

import com.vztekoverflow.cilostazol.CILOSTAZOLLanguage;
import junit.framework.TestCase;
import org.graalvm.polyglot.Source;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * You have to first build C# projects in test resources:  {@value  _directory}.
 * <p>
 * Build it with configuration: {@value _directory} and .NET version: {@value _dotnetVersion}
 */
public abstract class TestBase extends TestCase {
    protected static final String _directory = "src/test/resources/TypeParsingTestTargets";
    protected static final String _configuration = "Release";
    protected static final String _dotnetVersion = "net7.0";

    protected Path getDllPath(String projectName) {
        return Paths.get(_directory, projectName, String.format("bin/%s/%s", _configuration, _dotnetVersion), projectName + ".dll");
    }

    protected Source getSourceFromProject(String projectName) throws IOException {
        return Source.newBuilder(
                        CILOSTAZOLLanguage.ID,
                        org.graalvm.polyglot.io.ByteSequence.create(Files.readAllBytes(getDllPath(projectName))), projectName)
                .build();
    }

    protected Source[] getSourcesFromProjects(String... projectNames) throws IOException {
        Source[] sources = new Source[projectNames.length];
        for (int i = 0; i < projectNames.length; i++) {
            sources[i] = getSourceFromProject(projectNames[i]);
        }
        return sources;
    }
}
