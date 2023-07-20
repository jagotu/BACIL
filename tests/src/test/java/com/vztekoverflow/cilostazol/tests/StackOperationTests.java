package com.vztekoverflow.cilostazol.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class StackOperationTests extends TestBase {
  @Test
  public void simpleCall() {
    var result =
        runTestFromCode(
            """
                    string temp = "ABA";
                    return 1;
                    """);

    assertEquals(1, result.exitCode());
  }

  @Test
  public void binaryLessThanInt32() {
    var result =
        runTestFromCode(
            """
                int a = 42;
                if (a < 43)
                {
                    return 1;
                }
                else
                {
                    return 0;
                }
                """);

    assertEquals(1, result.exitCode());
  }

  @Test
  public void binaryLessThanInt64() {
    var result =
        runTestFromCode(
            """
                long a = 42_000_000_000_000_000L;
                if (a < 43_000_000_000_000_000L)
                {
                    return 1;
                }
                else
                {
                    return 0;
                }
                """);

    assertEquals(1, result.exitCode());
  }

  @Test
  public void binaryLessThanFloat32() {
    var result =
        runTestFromCode(
            """
                    float a = 1.0f;
                    if (a < 43.0f)
                    {
                        return 1;
                    }
                    else
                    {
                        return 0;
                    }
                    """);

    assertEquals(1, result.exitCode());
  }

  @Test
  public void binaryLessThanFloat64() {
    var result =
        runTestFromCode(
            """
                    double a = 1.0;
                    if (a < 43.0d)
                    {
                        return 1;
                    }
                    else
                    {
                        return 0;
                    }
                    """);

    assertEquals(1, result.exitCode());
  }

  @Test
  public void binaryLessThanEqualsInt32() {
    var result =
        runTestFromCode(
            """
                int a = 42;
                if (a <= 42)
                {
                    return 1;
                }
                else
                {
                    return 0;
                }
                """);

    assertEquals(1, result.exitCode());
  }

  @Test
  public void binaryLessThanEqualsInt64() {
    var result =
        runTestFromCode(
            """
                  long a = 42_000_000_000_000_000L;
                  if (a <= 42_000_000_000_000_000L)
                  {
                      return 1;
                  }
                  else
                  {
                      return 0;
                  }
                  """);

    assertEquals(1, result.exitCode());
  }

  @Test
  public void binaryLessThanEqualsFloat32() {
    var result =
        runTestFromCode(
            """
                      float a = 1.0f;
                      if (a <= 1.0f)
                      {
                          return 1;
                      }
                      else
                      {
                          return 0;
                      }
                      """);

    assertEquals(1, result.exitCode());
  }

  @Test
  public void binaryLessThanEqualsFloat64() {
    var result =
        runTestFromCode(
            """
                        double a = 1.0;
                        if (a <= 1.0d)
                        {
                            return 1;
                        }
                        else
                        {
                            return 0;
                        }
                        """);

    assertEquals(1, result.exitCode());
  }
}
