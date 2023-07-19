package com.vztekoverflow.cilostazol.tests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StackOperationTests extends TestBase
{
  @Test
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
                            string temp = "ABA";
                            return 1;
                        }
                    }
                  }
                    """);

    assertEquals(1, result.exitCode());
  }
}
