package com.vztekoverflow.cilostazol.tests;

import com.vztekoverflow.cilostazol.launcher.CILOSTAZOLLauncher;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import junit.framework.TestCase;
import org.intellij.lang.annotations.Language;

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

  /** Can be parallelized. */
  protected Path getDllPath(String projectName) {
    return Paths.get(directoryDllTests, projectName, "bin", projectName + ".dll");
  }

  /** Can NOT be parallelized! */
  protected Path getDllFromSourceCode(@Language("cs") String code) {
    // replace content of the dummy file with the source code
    try {
      if (!java.nio.file.Files.exists(customTestSourceFilePath)) {
        java.nio.file.Files.createFile(customTestSourceFilePath);
      }
      java.nio.file.Files.writeString(customTestSourceFilePath, code);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    // build the dummy project
    try {
      Runtime.getRuntime()
          .exec(
              "dotnet build %s -c %s -f %s -o %s"
                  .formatted(customTestPath, configuration, dotnetVersion, customTestOutputPath));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // return the path to the dll of a dummy project
    return customTestDllPath;
  }

  /** Can NOT be parallelized! */
  protected Path getDllFromSourceFile(String sourceFile) {
    // read content of the file
    @Language("cs")
    String sourceCode;
    try {
      sourceCode = java.nio.file.Files.readString(Paths.get(testSourcesDirectory, sourceFile));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return getDllFromSourceCode(sourceCode);
  }

  /**
   * Runs a test with sourcecode from <code>dll</code> located at: <code>
   * tests\src\test\resources\BasicTests\*projectName*\bin\Release\.net7.0\*projectName*.dll</code>
   */
  protected CILOSTAZOLLauncher runTestFromDll(String projectName) {
    CILOSTAZOLLauncher launcher = new CILOSTAZOLLauncher();
    launcher.test(new String[] {getDllPath(projectName).toString()});
    return launcher;
  }

  protected CILOSTAZOLLauncher runTestFromFile(String sourceFile) {
    CILOSTAZOLLauncher launcher = new CILOSTAZOLLauncher();
    launcher.test(new String[] {getDllFromSourceFile(sourceFile).toString()});
    return launcher;
  }

  protected CILOSTAZOLLauncher runTestFromSourceCode(@Language("cs") String code) {
    CILOSTAZOLLauncher launcher = new CILOSTAZOLLauncher();
    launcher.test(new String[] {getDllFromSourceCode(code).toString()});
    return launcher;
  }
}
