package com.vztekoverflow.cilostazol.runtime.typesystem;

import com.vztekoverflow.cil.parser.cli.AssemblyIdentity;
import com.vztekoverflow.cilostazol.CILOSTAZOLLanguage;
import com.vztekoverflow.cilostazol.runtime.context.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.context.ContextProviderImpl;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import junit.framework.TestCase;
import org.graalvm.polyglot.Source;

/**
 * You have to first build C# projects in test resources: {@value _directory}.
 *
 * <p>Build it with configuration: {@value _directory} and .NET version: {@value _dotnetVersion}
 */
public abstract class TestBase extends TestCase {
  protected static final String _directory = "src/test/resources/TypeParsingTestTargets";
  protected static final String _configuration = "Release";
  protected static final String _dotnetVersion = "net7.0";

  protected CILOSTAZOLContext ctx;
  protected CILOSTAZOLLanguage lang;

  protected Path getDllPath(String projectName) {
    return Paths.get(_directory, projectName, "bin", projectName + ".dll");
  }

  protected Source getSourceFromProject(String projectName) throws IOException {
    return Source.newBuilder(
            CILOSTAZOLLanguage.ID,
            org.graalvm.polyglot.io.ByteSequence.create(
                Files.readAllBytes(getDllPath(projectName))),
            projectName)
        .build();
  }

  protected Source[] getSourcesFromProjects(String... projectNames) throws IOException {
    Source[] sources = new Source[projectNames.length];
    for (int i = 0; i < projectNames.length; i++) {
      sources[i] = getSourceFromProject(projectNames[i]);
    }
    return sources;
  }

  protected CILOSTAZOLContext init() {
    return init(new Path[0]);
  }

  protected CILOSTAZOLContext init(Path[] dllPaths) {
    this.lang = new CILOSTAZOLLanguage();
    this.ctx = new CILOSTAZOLContext(lang, dllPaths);
    ContextProviderImpl.getInstance().setContext(() -> ctx);
    return ContextProviderImpl.getInstance().getContext();
  }

  protected AssemblyIdentity getAssemblyID(String name) {
    return new AssemblyIdentity((short) 1, (short) 0, (short) 0, (short) 0, name);
  }
}
