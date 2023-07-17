package com.vztekoverflow.cilostazol.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.vztekoverflow.cilostazol.launcher.CILOSTAZOLLauncher;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;

@DisabledOnOs(value = OS.LINUX, disabledReason = "Avoid CI on Linux")
public class TestTemplates extends TestBase {

  /*
   Here is shown how to write tests that get sourcecode form:
    - dll:
      - you have to create a valid project in directory tests\src\test\resources\BasicTests
      and compile it before running tests
    - source code:
      - you have to write a valid C# code in a string (IntelliJ IDEA will highlight it when annotated with @Language("cs")
    - source file:
      - you have to write a valid C# code in a file in directory tests\src\test\resources\InterpreterTests\TestSources
  */

  // region Deprecated Test templates
  /** This is a template for tests that get sourcecode from dll. */
  @Test
  @Disabled("Paths to dlls are not setup")
  public void FromDllViaLauncher() {
    CILOSTAZOLLauncher launcher = runTestFromDllViaLauncher("Initial");

    int returnValue = launcher.getReturnValue();
    assertEquals(1, returnValue);
  }

  /** This is a template for tests that get sourcecode from string. */
  @Test
  @Disabled("Paths to dlls are not setup")
  public void FromSourceCodeViaLauncher() {
    var launcher =
        runTestFromCodeLauncher(
            """
      using System;
      namespace CustomTest
      {
        public class Program
        {
            public static int Main()
            {
                return 42;
            }
        }
      }""");

    int returnValue = launcher.getReturnValue();
    assertEquals(1, returnValue);
  }

  /**
   * This is a template for tests that get sourcecode from file. You can write top-level code in the
   * file.
   */
  @Test
  @Disabled("Paths to dlls are not setup")
  public void FromSourceFileViaLauncher() {
    CILOSTAZOLLauncher launcher = runTestFromFileLauncher("return.cs");

    int returnValue = launcher.getReturnValue();
    assertEquals(42, returnValue);
  }

  // endregion

  // region Test Templates

  /**
   * Use this for quick/additional tests who's core features are tested by File or DLL tests already
   */
  @Test
  public void FromCode() {
    var result = runTestFromCode("""
return 42;
""");

    assertEquals("", result.output());
    assertEquals(42, result.exitCode());
  }

  /**
   * Use this or {@link #runTestFromFile(String)} template for each major feature of the interpreter
   * (return, if, while, etc.)
   */
  @Test
  public void FromDll() {
    var result = runTestFromDll("Initial");

    assertEquals("", result.output());
    assertEquals(42, result.exitCode());
  }

  /**
   * Use this or {@link #FromDll()} template for each major feature of the interpreter (return, if,
   * while, etc.)
   */
  @Test
  public void FromFile() {
    var result = runTestFromFile("return.cs");

    assertEquals("", result.output());
    assertEquals(42, result.exitCode());
  }

  // endregion
}
