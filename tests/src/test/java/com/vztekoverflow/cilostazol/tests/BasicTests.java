package com.vztekoverflow.cilostazol.tests;

import com.vztekoverflow.cilostazol.launcher.CILOSTAZOLLauncher;
import org.junit.Ignore;
import org.junit.Test;
// import org.junit.jupiter.api.TestInstance;
// import org.junit.jupiter.params.ParameterizedTest;
// import org.junit.jupiter.params.provider.ValueSource;

// @TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BasicTests extends TestBase {
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
  @Ignore("Does not work with Console.WriteLine()")
  public void FromDllViaLauncher() {
    CILOSTAZOLLauncher launcher = runTestFromDllViaLauncher("Initial");

    int returnValue = launcher.getReturnValue();
    assertEquals(1, returnValue);
  }

  /** This is a template for tests that get sourcecode from string. */
  @Test
  @Ignore("Does not work with Console.WriteLine()")
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
  @Ignore("Does not work with Console.WriteLine()")
  public void FromSourceFileViaLauncher() {
    CILOSTAZOLLauncher launcher = runTestFromFileLauncher("return.cs");

    int returnValue = launcher.getReturnValue();
    assertEquals(1, returnValue);
  }

  // endregion

  // TODO: this test will need some modifications after args are implemented
  // region Test Templates

  /**
   * Use this for quick/additional tests who's core features are tested by File or DLL tests already
   */
  public void testFromCode() {
    // TODO: this test will need to be redone after args are implemented
    var result =
        runTestFromCode(
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

    assertEquals("", result.output());
    assertEquals(1, result.exitCode());
  }

  /**
   * Use this or {@link #testFromFile()} template for each major feature of the interpreter (return,
   * if, while, etc.)
   */
  public void testFromDll() {
    var result = runTestFromDll("Initial");

    assertEquals("", result.output());
    assertEquals(1, result.exitCode());
  }

  /**
   * Use this or {@link #testFromDll()} template for each major feature of the interpreter (return,
   * if, while, etc.)
   */
  public void testReturn() {
    var result = runTestFromFile("return.cs");

    assertEquals("TEST\r\n", result.output());
    assertEquals(42, result.exitCode());
  }

  // endregion

  // region Tests

  //  @ParameterizedTest
  //  @ValueSource(strings = {
  //          "return.cs",
  //          "return0.cs",
  ////          "ConsoleWriteline.cs",
  ////          "assignment.cs",
  //  })
  //  public void fileTests(String fileName) {
  //    //TODO: replace Value source with MethodSource that gets all files in TestSources directory
  //    //TODO: replace assertEquals of the ouptut with adequate gold files
  //    var result = runTestFromFile(fileName);
  //
  ////    assertEquals("TEST\r\n", result.output());
  //    assertEquals(1, result.exitCode());
  //  }

  // endregion
}
