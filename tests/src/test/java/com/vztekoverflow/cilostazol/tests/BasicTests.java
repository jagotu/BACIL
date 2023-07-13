package com.vztekoverflow.cilostazol.tests;

import com.vztekoverflow.cilostazol.launcher.CILOSTAZOLLauncher;

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

  // region Test templates
  /** This is a template for tests that get sourcecode from dll. */
  public void testFromDll() {
    CILOSTAZOLLauncher launcher = runTestFromDll("Initial");

    int returnValue = launcher.getReturnValue();
    assertEquals(1, returnValue);
  }

  /** This is a template for tests that get sourcecode from string. */
  public void testFromSourceCode() {
    var launcher =
        runTestFromSourceCode(
            """
      using System;
      namespace CustomTest
      {
        public class Program
        {
            public static int Main(int a)
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
  public void testFromSourceFile() {
    CILOSTAZOLLauncher launcher = runTestFromFile("return.cs");

    int returnValue = launcher.getReturnValue();
    assertEquals(1, returnValue);
  }

  // endregion
}
