package com.vztekoverflow.cilostazol.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NonVirtualCallsTests extends TestBase {
  @Test
  @Disabled("Not implemented yet - WIP")
  public void simpleCall() {
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
                    return Foo();
                }

                public static int Foo()
                {
                    return 42;
                }
            }
          }
            """);

    assertEquals(42, result.exitCode());
  }

  @Test
  @Disabled("Not implemented yet - waiting for LDSTR")
  public void consoleWriteLine() {
    var result =
        runTestFromCode("""
            Console.WriteLine("Hello World!");
            """);
    assertEquals(0, result.exitCode());
    assertEquals(String.format("Hello World!%s", System.lineSeparator()), result.output());
  }

  @Test
  @Disabled("Not implemented yet - waiting for LDSTR")
  public void nestedConsoleWriteLine() {
    var launcher =
        runTestFromCode(
            """
          using System;
          namespace CustomTest
          {
            public class Program
            {
                public static void Main()
                {
                    Foo();
                }

                public static void Foo()
                {
                    Console.WriteLine("Hello World!");
                }
            }
          }
            """);

    assertEquals(0, launcher.exitCode());
    assertEquals(String.format("Hello World!%s", System.lineSeparator()), launcher.output());
  }
}
