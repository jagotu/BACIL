package com.vztekoverflow.cilostazol.tests;

import static com.vztekoverflow.cilostazol.launcher.CILOSTAZOLLauncher.LANGUAGE_ID;

import com.vztekoverflow.cilostazol.launcher.CILOSTAZOLLauncher;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import junit.framework.TestCase;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.BeforeAll;

public abstract class TestBase extends TestCase {
  private static final String directoryDllTests = "src/test/resources/BasicTests";
  private static final String directoryCustomTest =
      "src/test/resources/InterpreterTests/CustomTest";
  private static final Path customTestPath = Paths.get(directoryCustomTest);
  private static final Path customTestSourceFilePath = Paths.get(directoryCustomTest, "Program.cs");
  private static final Path customTestOutputPath = Paths.get(directoryCustomTest, "bin");
  private static final Path customTestDllPath =
      Paths.get(directoryCustomTest, "bin", "CustomTest.dll");
  private static final String testSourcesDirectory =
      "src/test/resources/InterpreterTests/TestSources";
  private static final String configuration = "Release";
  private static final String dotnetVersion = "net7.0";

  private OutputStream outputStream;
  private Context context;

  /**
   * Can be parallelized. Project must be at {@value directoryDllTests} and must be compiled before
   * running tests.
   */
  protected Path getDllPathFromProject(String projectName) {
    return Paths.get(directoryDllTests, projectName, "bin", projectName + ".dll");
  }

  /** Can NOT be parallelized! */
  protected Path getDllPathFromSourceCode(@Language("cs") String code) {
    // replace content of the dummy file with the source code
    writeSourceCode(code);

    // build the dummy project
    buildSource();

    // return the path to the dll of a dummy project
    return customTestDllPath;
  }

  private static void writeSourceCode(@Language("cs") String code) {
    try {
      if (!java.nio.file.Files.exists(customTestSourceFilePath)) {
        java.nio.file.Files.createFile(customTestSourceFilePath);
      }
      java.nio.file.Files.writeString(customTestSourceFilePath, code);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static void buildSource() {
    try {
      Runtime.getRuntime()
          .exec(
              "dotnet build %s -c %s -f %s -o %s"
                  .formatted(customTestPath, configuration, dotnetVersion, customTestOutputPath));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /** Can NOT be parallelized! */
  protected Path getDllPathFromSourceFile(String sourceFile) {
    // read content of the file
    @Language("cs")
    String sourceCode;
    sourceCode = getSourceCode(sourceFile);

    return getDllPathFromSourceCode(sourceCode);
  }

  private static @Language("cs") String getSourceCode(String sourceFile) {
    @Language("cs")
    String sourceCode;
    try {
      sourceCode = java.nio.file.Files.readString(Paths.get(testSourcesDirectory, sourceFile));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return sourceCode;
  }

  /**
   * Runs a test with sourcecode from <code>dll</code> located at: <code>
   * tests\src\test\resources\BasicTests\*projectName*\bin\Release\.net7.0\*projectName*.dll</code>
   */
  protected CILOSTAZOLLauncher runTestFromDll(String projectName) {
    CILOSTAZOLLauncher launcher = new CILOSTAZOLLauncher();
    launcher.test(new String[] {getDllPathFromProject(projectName).toString()});
    return launcher;
  }

  protected CILOSTAZOLLauncher runTestFromFile(String sourceFile) {
    CILOSTAZOLLauncher launcher = new CILOSTAZOLLauncher();
    launcher.test(new String[] {getDllPathFromSourceFile(sourceFile).toString()});
    return launcher;
  }

  protected CILOSTAZOLLauncher runTestFromCode(@Language("cs") String code) {
    CILOSTAZOLLauncher launcher = new CILOSTAZOLLauncher();
    launcher.test(new String[] {getDllPathFromSourceCode(code).toString()});
    return launcher;
  }

  protected String runTest(@Language("cs") String sourceCode) {
    var sourceFile = getDllPathFromSourceCode(sourceCode).toFile();

    var result = context.eval(getSource(sourceFile)).asInt();
    return outputStream.toString();
  }

  @BeforeAll
  public void setUp() {
    outputStream = new ByteArrayOutputStream();
    context = setupContext().build();
  }

  private Context.Builder setupContext() {
    return Context.newBuilder(LANGUAGE_ID).out(outputStream).err(outputStream).allowAllAccess(true);
  }

  private Source getSource(File sourceFile) {
    try {
      return Source.newBuilder(LANGUAGE_ID, sourceFile).build();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
