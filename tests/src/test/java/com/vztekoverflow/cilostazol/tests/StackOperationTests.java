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

  @Test
  public void binaryGreaterThanInt32() {
    var result =
        runTestFromCode(
            """
                    int a = 42;
                    if (a > 41)
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
  public void binaryGreaterThanInt64() {
    var result =
        runTestFromCode(
            """
                    long a = 42_000_000_000_000_000L;
                    if (a > 41_000_000_000_000_000L)
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
  public void binaryGreaterThanFloat32() {
    var result =
        runTestFromCode(
            """
                        float a = 1.0f;
                        if (a > 0.0f)
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
  public void binaryGreaterThanFloat64() {
    var result =
        runTestFromCode(
            """
                        double a = 1.0;
                        if (a > 0.0d)
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
  public void binaryGreaterThanEqualsInt32() {
    var result =
        runTestFromCode(
            """
                        int a = 42;
                        if (a >= 42)
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
  public void binaryGreaterThanEqualsInt64() {
    var result =
        runTestFromCode(
            """
                        long a = 42_000_000_000_000_000L;
                        if (a >= 42_000_000_000_000_000L)
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
  public void binaryGreaterThanEqualsFloat32() {
    var result =
        runTestFromCode(
            """
                            float a = 1.0f;
                            if (a >= 1.0f)
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
  public void binaryGreaterThanEqualsFloat64() {
    var result =
        runTestFromCode(
            """
                            double a = 1.0;
                            if (a >= 1.0d)
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
  public void binaryEqualsInt32() {
    var result =
        runTestFromCode(
            """
                        int a = 42;
                        if (a == 42)
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
  public void binaryEqualsInt64() {
    var result =
        runTestFromCode(
            """
                        long a = 42_000_000_000_000_000L;
                        if (a == 42_000_000_000_000_000L)
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
  public void binaryEqualsFloat32() {
    var result =
        runTestFromCode(
            """
                        float a = 1.0f;
                        if (a == 1.0f)
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
  public void binaryEqualsFloat64() {
    var result =
        runTestFromCode(
            """
                        double a = 1.0;
                        if (a == 1.0d)
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
  public void binaryNotEqualsInt32() {
    var result =
        runTestFromCode(
            """
                            int a = 42;
                            if (a != 41)
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
  public void binaryNotEqualsInt64() {
    var result =
        runTestFromCode(
            """
                            long a = 42_000_000_000_000_000L;
                            if (a != 41_000_000_000_000_000L)
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
  public void binaryNotEqualsFloat32() {
    var result =
        runTestFromCode(
            """
                            float a = 1.0f;
                            if (a != 0.0f)
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
  public void binaryNotEqualsFloat64() {
    var result =
        runTestFromCode(
            """
                            double a = 1.0;
                            if (a != 0.0d)
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
